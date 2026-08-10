package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.FamilyRequest
import com.ralphmarondev.registry.dto.FamilyResponse
import com.ralphmarondev.registry.entity.Member
import com.ralphmarondev.registry.enums.RelationshipToHead
import com.ralphmarondev.registry.mapper.toFamily
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.FamilyRepository
import com.ralphmarondev.registry.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class FamilyService(
    private val familyRepository: FamilyRepository,
    private val memberRepository: MemberRepository
) {
    fun getAll(): List<FamilyResponse> {
        return familyRepository.findAll()
            .map { it.toResponse() }
            .filter { !it.isDeleted }
    }

    fun getById(id: Long): FamilyResponse {
        return familyRepository.findById(id)
            .orElseThrow { RuntimeException("Family not found.") }
            .toResponse()
    }

    fun getByCode(code: String): FamilyResponse? {
        return familyRepository.findByCode(code)?.toResponse()
    }

    fun create(request: FamilyRequest): FamilyResponse {
        if (familyRepository.findByCode(request.code) != null) {
            throw RuntimeException("Family code already exists.")
        }
        val family = request.toFamily()
        val savedFamily = familyRepository.save(family)
        val familyHead = request.head
            ?: throw IllegalStateException("Family head is not specified.")

        val member = Member(
            family = family.copy(id = savedFamily.id),
            firstName = familyHead.firstName,
            middleName = familyHead.middleName,
            lastName = familyHead.lastName,
            suffix = familyHead.suffix,
            sex = familyHead.sex,
            dateOfBirth = familyHead.dateOfBirth,
            placeOfBirth = familyHead.placeOfBirth,
            phoneNumber = familyHead.phoneNumber,
            civilStatus = familyHead.civilStatus,
            nationality = familyHead.nationality,
            religion = familyHead.religion,
            occupation = familyHead.occupation,
            educationalAttainment = familyHead.educationalAttainment,
            isHead = true,
            relationshipToHead = RelationshipToHead.SELF,
        )
        val savedFamilyHead = memberRepository.save(member).toResponse()
        val response = family.toResponse()
        return response.copy(head = savedFamilyHead)
    }

    fun update(id: Long, request: FamilyRequest): FamilyResponse {
        val existing = familyRepository.findById(id)
            .orElseThrow { RuntimeException("Family not found.") }

        val updatedFamily = existing.copy(
            code = request.code,
            name = request.name,
            blockNumber = request.blockNumber,
            barangay = request.barangay,
            city = request.city,
            province = request.province,
            landline = request.landline,
            updateDate = LocalDateTime.now()
        )
        return familyRepository.save(updatedFamily).toResponse()
    }

    fun delete(id: Long) {
        val existing = familyRepository.findById(id)
            .orElseThrow { RuntimeException("Family not found.") }

        val deleted = existing.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )

        familyRepository.save(deleted)
    }

    fun batch(requests: List<FamilyRequest>): List<FamilyResponse> {
        return requests.map { create(it) }
    }
}