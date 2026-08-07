package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.MemberRequest
import com.ralphmarondev.registry.dto.MemberResponse
import com.ralphmarondev.registry.entity.Member
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.FamilyRepository
import com.ralphmarondev.registry.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val familyRepository: FamilyRepository
) {
    fun getAll(): List<MemberResponse> {
        return memberRepository.findAll()
            .map { it.toResponse() }
            .filter { !it.isDeleted }
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
        return memberRepository.save(member).toResponse()
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
        return memberRepository.save(updated).toResponse()
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
}