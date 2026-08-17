package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.*
import com.ralphmarondev.registry.entity.Account
import com.ralphmarondev.registry.entity.Member
import com.ralphmarondev.registry.enums.RelationshipToHead
import com.ralphmarondev.registry.mapper.toFamily
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.AccountRepository
import com.ralphmarondev.registry.repository.FamilyRepository
import com.ralphmarondev.registry.repository.MemberRepository
import com.ralphmarondev.registry.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class FamilyService(
    private val familyRepository: FamilyRepository,
    private val memberRepository: MemberRepository,
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository
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
        println("Family service... create($request)")
        if (familyRepository.findByCode(request.code) != null) {
            throw RuntimeException("Family code already exists.")
        }
        println("Family Code not exists.")
        val familyHeadInformation = request.head
            ?: throw IllegalStateException("Family head information is not specified.")
        println("Family head information exists.")
        val familyHeadAccount = request.account
            ?: throw IllegalStateException("Family head account is not specified.")
        println("Family head account exists.")

        val family = request.toFamily()
        val savedFamily = familyRepository.save(family)
        println("Family saved: $savedFamily.")
        val headResponse = registerFamilyHeadInformation(
            request = familyHeadInformation.copy(familyId = savedFamily.id)
        )
        println("Family head response: $headResponse")
        val accountResponse = createFamilyHeadAccount(
            request = familyHeadAccount.copy(memberId = headResponse.id)
        )
        println("Family account response: $accountResponse")
        val memberCount = countFamilyMember(savedFamily.id)
        val familyResponse = family.toResponse()
        val response = familyResponse.copy(
            head = headResponse,
            account = accountResponse,
            memberCount = memberCount
        )
        println("Family response: $response")
        return response
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

    private fun registerFamilyHeadInformation(
        request: MemberRequest
    ): MemberResponse {
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
            isHead = true,
            relationshipToHead = RelationshipToHead.SELF,
        )
        return memberRepository.save(member).toResponse()
    }

    private fun createFamilyHeadAccount(
        request: RegisterRequest
    ): AccountResponse {
        val role = roleRepository.findById(request.roleId)
            .orElseThrow { IllegalStateException("Role not found.") }

        val account = Account(
            username = request.username,
            password = request.password,
            email = request.email,
            role = role
        )
        val saved = accountRepository.save(account)
        return AccountResponse(
            id = saved.id,
            username = saved.username,
            email = saved.email,
            role = role.toResponse()
        )
    }

    private fun countFamilyMember(familyId: Long): Int {
        return memberRepository.countByFamilyId(familyId)
    }
}