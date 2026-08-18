package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.MemberRequest
import com.ralphmarondev.registry.dto.MemberResponse
import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.entity.Member
import com.ralphmarondev.registry.entity.MemberProgram
import com.ralphmarondev.registry.exception.ResourceNotFoundException
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.FamilyRepository
import com.ralphmarondev.registry.repository.MemberProgramsRepository
import com.ralphmarondev.registry.repository.MemberRepository
import com.ralphmarondev.registry.repository.ProgramRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val familyRepository: FamilyRepository,
    private val programRepository: ProgramRepository,
    private val memberProgramsRepository: MemberProgramsRepository
) {
    fun getAll(): List<MemberResponse> {
        return memberRepository.findAll()
            .filter { !it.isDeleted }
            .map { toMemberResponse(it) }
    }

    fun getById(id: Long): MemberResponse {
        val member = memberRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Member with id $id not found.") }
        return toMemberResponse(member)
    }

    fun getByFamilyId(familyId: Long): List<MemberResponse> {
        familyRepository.findById(familyId)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Family not found.") }
        return memberRepository.findByFamilyId(familyId)
            .filter { !it.isDeleted }
            .map { toMemberResponse(it) }
    }

    fun create(request: MemberRequest): MemberResponse {
        val family = familyRepository.findById(request.familyId)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Family not found.") }

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
        val programs = addPrograms(
            member = savedMember,
            programIds = request.beneficiaryPrograms ?: emptyList()
        )
        return savedMember.toResponse().copy(beneficiaryPrograms = programs)
    }

    fun update(id: Long, request: MemberRequest): MemberResponse {
        val existing = memberRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Member with id $id not found.") }

        val family = familyRepository.findById(request.familyId)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Family not found.") }

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
        memberProgramsRepository.deleteAllByMemberId(id)

        val programs = addPrograms(
            member = savedMember,
            programIds = request.beneficiaryPrograms ?: emptyList()
        )

        return savedMember.toResponse().copy(beneficiaryPrograms = programs)
    }

    fun delete(id: Long) {
        val existing = memberRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Member not found.") }
        val deleted = existing.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )
        memberRepository.save(deleted)
    }

    fun batch(requests: List<MemberRequest>): List<MemberResponse> {
        return requests.map { create(it) }
    }

    private fun addPrograms(
        member: Member,
        programIds: List<Long>
    ): List<ProgramResponse> {
        return programIds.map { programId ->
            val program = programRepository.findById(programId)
                .filter { !it.isDeleted }
                .orElseThrow { ResourceNotFoundException("Program with id $programId not found.") }
            memberProgramsRepository.save(
                MemberProgram(
                    member = member,
                    program = program
                )
            )
            program.toResponse()
        }
    }

    private fun toMemberResponse(
        member: Member
    ): MemberResponse {
        val programs = getPrograms(member.id)
        return member.toResponse().copy(beneficiaryPrograms = programs)
    }

    private fun getPrograms(
        memberId: Long
    ): List<ProgramResponse> {
        return memberProgramsRepository
            .findByMemberId(memberId)
            .filter { !it.isDeleted }
            .filter { !it.program.isDeleted }
            .map { it.program.toResponse() }
    }
}