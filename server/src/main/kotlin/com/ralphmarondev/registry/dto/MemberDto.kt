package com.ralphmarondev.registry.dto

import com.ralphmarondev.registry.enums.CivilStatus
import com.ralphmarondev.registry.enums.RelationshipToHead
import com.ralphmarondev.registry.enums.Sex
import java.time.LocalDate

data class MemberRequest(
    val familyId: Long,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val suffix: String? = null,
    val sex: Sex,
    val dateOfBirth: LocalDate,
    val placeOfBirth: String,
    val phoneNumber: String,
    val civilStatus: CivilStatus,
    val nationality: String,
    val religion: String,
    val occupation: String,
    val educationalAttainment: String,
    val isHead: Boolean,
    val relationshipToHead: RelationshipToHead,
    val isIndigenous: Boolean,
    val indigenousGroup: String? = null,
    val beneficiaryPrograms: List<Long> = emptyList()
)

data class MemberResponse(
    val id: Long,
    val family: Long,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val suffix: String?,
    val sex: Sex,
    val dateOfBirth: LocalDate,
    val placeOfBirth: String,
    val phoneNumber: String,
    val civilStatus: CivilStatus,
    val nationality: String,
    val religion: String,
    val occupation: String,
    val educationalAttainment: String,
    val isHead: Boolean,
    val relationshipToHead: RelationshipToHead,
    val isIndigenous: Boolean,
    val indigenousGroup: String? = null,
    val beneficiaryPrograms: List<BeneficiaryProgramResponse>,
    val isDeleted: Boolean
)

data class BeneficiaryProgramResponse(
    val id: Long = 0,
    val name: String = ""
)