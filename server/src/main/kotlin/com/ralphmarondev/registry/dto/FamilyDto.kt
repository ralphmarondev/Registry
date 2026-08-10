package com.ralphmarondev.registry.dto

import com.ralphmarondev.registry.enums.HouseholdType
import com.ralphmarondev.registry.enums.HousingOwnership
import com.ralphmarondev.registry.enums.RegistrationStatus
import java.time.LocalDateTime

data class FamilyRequest(
    val code: String,
    val name: String,
    val blockNumber: String,
    val barangay: String,
    val city: String,
    val province: String,
    val landline: String? = null,
    val householdNumber: String? = null,
    val householdType: HouseholdType,
    val housingOwnership: HousingOwnership,
    val registrationStatus: RegistrationStatus = RegistrationStatus.PENDING,
    val head: MemberRequest? = null
)

data class FamilyResponse(
    val id: Long,
    val code: String,
    val name: String,
    val blockNumber: String,
    val barangay: String,
    val city: String,
    val province: String,
    val landline: String? = null,
    val householdNumber: String? = null,
    val householdType: HouseholdType,
    val housingOwnership: HousingOwnership,
    val registrationStatus: RegistrationStatus = RegistrationStatus.PENDING,
    val head: MemberResponse? = null,
    val memberCount: Int = 0,
    val isDeleted: Boolean = false,
    val createDate: LocalDateTime,
    val updateDate: LocalDateTime?
)