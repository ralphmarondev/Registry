package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.dto.MemberRequest
import com.ralphmarondev.registry.dto.MemberResponse
import com.ralphmarondev.registry.entity.Member
import com.ralphmarondev.registry.entity.MemberProgram
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.ProgramRepository
import com.ralphmarondev.registry.repository.FamilyRepository
import com.ralphmarondev.registry.repository.MemberProgramsRepository
import com.ralphmarondev.registry.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val familyRepository: FamilyRepository,
    private val beneficiaryProgramRepository: ProgramRepository,
    private val memberBeneficiaryRepository: MemberProgramsRepository
) {
    fun getAll(): List<MemberResponse> {
        return memberRepository.findAll()
            .filter { !it.isDeleted }
            .map { member ->
                val programs = getBeneficiaryPrograms(member.id)
                member.toResponse().copy(beneficiaryPrograms = programs)
            }
    }

    fun getById(id: Long): MemberResponse {
        return memberRepository.findById(id)
            .orElseThrow { RuntimeException("Member not found.") }
            .toResponse()
    }

    fun create(request: MemberRequest): MemberResponse {
        val family = familyRepository.findById(request.familyId)
            .orElseThrow { RuntimeException("Family not found.") }

        val member = Member(
            family = family,
            firstName = request.firstName,
            middleName = request.middleName,
            lastName = request.lastName,
            suffix = request.suffix,
            sex = request.sex,
            dateOfBirth = request.dateOfBirth,
            placeOfBirth = request.placeOfBirth,
            phoneNumber = request.phoneNumber,
            civilStatus = request.civilStatus,
            nationality = request.nationality,
            religion = request.religion,
            occupation = request.occupation,
            educationalAttainment = request.educationalAttainment,
            isHead = request.isHead,
            relationshipToHead = request.relationshipToHead,
            isIndigenous = request.isIndigenous,
            indigenousGroup = request.indigenousGroup
        )
        val savedMember = memberRepository.save(member)
        val programs = mutableListOf<ProgramResponse>()
        request.beneficiaryPrograms.forEach { programId ->
            val program = beneficiaryProgramRepository.findById(programId)
                .orElseThrow { RuntimeException("Beneficiary program not found.") }
            memberBeneficiaryRepository.save(
                MemberProgram(
                    member = savedMember,
                    program = program
                )
            )
            programs.add(program.toResponse())
        }
        return savedMember.toResponse().copy(beneficiaryPrograms = programs)
    }

    fun update(id: Long, request: MemberRequest): MemberResponse {
        val existing = memberRepository.findById(id)
            .orElseThrow { RuntimeException("Member not found.") }

        val family = familyRepository.findById(request.familyId)
            .orElseThrow { RuntimeException("Family not found.") }

        val updated = existing.copy(
            family = family,
            firstName = request.firstName,
            middleName = request.middleName,
            lastName = request.lastName,
            suffix = request.suffix,
            sex = request.sex,
            dateOfBirth = request.dateOfBirth,
            placeOfBirth = request.placeOfBirth,
            phoneNumber = request.phoneNumber,
            civilStatus = request.civilStatus,
            nationality = request.nationality,
            religion = request.religion,
            occupation = request.occupation,
            educationalAttainment = request.educationalAttainment,
            isHead = request.isHead,
            relationshipToHead = request.relationshipToHead,
            isIndigenous = request.isIndigenous,
            indigenousGroup = request.indigenousGroup,
            updateDate = LocalDateTime.now()
        )
        val savedMember = memberRepository.save(updated)
        memberBeneficiaryRepository.deleteAllByMemberId(id)

        request.beneficiaryPrograms.forEach { programId ->
            val program = beneficiaryProgramRepository.findById(programId)
                .orElseThrow { RuntimeException("Beneficiary program not found.") }
            memberBeneficiaryRepository.save(MemberProgram(member = savedMember, program = program))
        }

        return savedMember.toResponse()
    }

    fun delete(id: Long) {
        val existing = memberRepository.findById(id)
            .orElseThrow { RuntimeException("Member not found.") }
        val deleted = existing.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )
        memberRepository.save(deleted)
    }

    private fun getBeneficiaryPrograms(memberId: Long): List<ProgramResponse> {
        return memberBeneficiaryRepository.findByMemberId(memberId)
            .filter { !it.isDeleted }
            .map {
                ProgramResponse(
                    id = it.program.id,
                    name = it.program.name,
                    description = it.program.description,
                    createDate = it.program.createDate,
                    updateDate = it.program.updateDate
                )
            }
    }

    fun batch(requests: List<MemberRequest>): List<MemberResponse> {
        return requests.map { create(it) }
    }
}