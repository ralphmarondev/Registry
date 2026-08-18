package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.*
import com.ralphmarondev.registry.entity.Account
import com.ralphmarondev.registry.entity.Family
import com.ralphmarondev.registry.entity.Member
import com.ralphmarondev.registry.enums.RelationshipToHead
import com.ralphmarondev.registry.exception.PasswordEncodingException
import com.ralphmarondev.registry.exception.ResourceAlreadyExistsException
import com.ralphmarondev.registry.exception.ResourceNotFoundException
import com.ralphmarondev.registry.mapper.toFamily
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.AccountRepository
import com.ralphmarondev.registry.repository.FamilyRepository
import com.ralphmarondev.registry.repository.MemberRepository
import com.ralphmarondev.registry.repository.RoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class FamilyService(
    private val familyRepository: FamilyRepository,
    private val memberRepository: MemberRepository,
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getAll(): List<FamilyResponse> {
        return familyRepository.findAll()
            .filter { !it.isDeleted }
            .map { it.toResponse() }
    }

    fun getById(id: Long): FamilyResponse {
        return familyRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Family not found.") }
            .toResponse()
    }

    fun getByCode(code: String): FamilyResponse {
        return familyRepository.findByCode(code)
            ?.takeIf { !it.isDeleted }
            ?.toResponse()
            ?: throw ResourceNotFoundException("Family not found.")
    }

    fun create(request: FamilyRequest): FamilyResponse {
        val existingFamily = familyRepository.findByCode(request.code)

        if (existingFamily != null && !existingFamily.isDeleted) {
            throw ResourceNotFoundException("Family not found.")
        }
        val familyHeadInformation = request.head
            ?: throw IllegalStateException("Family head information is not specified.")
        val familyHeadAccount = request.account
            ?: throw IllegalStateException("Family head account is not specified.")
        val family = familyRepository.save(request.toFamily())
        val headResponse = registerFamilyHeadInformation(
            family = family,
            request = familyHeadInformation
        )

        val accountResponse = createFamilyHeadAccount(
            member = memberRepository.findById(headResponse.id)
                .orElseThrow { ResourceNotFoundException("Family head not found.") },
            request = familyHeadAccount
        )
        return family.toResponse().copy(
            head = headResponse,
            account = accountResponse,
            memberCount = countFamilyMember(family.id)
        )
    }

    fun update(id: Long, request: FamilyRequest): FamilyResponse {
        val existingFamily = familyRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { RuntimeException("Family not found.") }

        val existingWithCode = familyRepository.findByCode(request.code)

        if (existingFamily.code != request.code &&
            existingWithCode != null &&
            !existingWithCode.isDeleted
        ) {
            throw ResourceAlreadyExistsException("Family code '${request.code}' already exists.")
        }

        val updatedFamily = existingFamily.copy(
            code = request.code,
            name = request.name,
            blockNumber = request.blockNumber,
            barangay = request.barangay,
            city = request.city,
            province = request.province,
            landline = request.landline,
            householdNumber = request.householdNumber,
            householdType = request.householdType,
            housingOwnership = request.housingOwnership,
            registrationStatus = request.registrationStatus,
            updateDate = LocalDateTime.now()
        )
        return familyRepository.save(updatedFamily).toResponse()
    }

    fun delete(id: Long) {
        val existingFamily = familyRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { RuntimeException("Family not found.") }

        val deletedFamily = existingFamily.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )

        familyRepository.save(deletedFamily)
    }

    fun batch(requests: List<FamilyRequest>): List<FamilyResponse> {
        return requests.map { create(it) }
    }

    private fun registerFamilyHeadInformation(
        family: Family,
        request: MemberRequest
    ): MemberResponse {
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
        member: Member,
        request: RegisterRequest
    ): AccountResponse {
        if (accountRepository.findByUsername(request.username) != null) {
            throw ResourceAlreadyExistsException("Username already taken.")
        }

        val role = roleRepository.findById(request.roleId)
            .orElseThrow { ResourceNotFoundException("Role not found.") }

        val password = passwordEncoder.encode(request.password)
            ?: throw PasswordEncodingException("Failed to encode password.")

        val account = Account(
            username = request.username,
            password = password,
            email = request.email,
            role = role,
            member = member
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