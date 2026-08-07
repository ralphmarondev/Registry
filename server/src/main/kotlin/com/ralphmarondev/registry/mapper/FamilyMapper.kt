package com.ralphmarondev.registry.mapper

import com.ralphmarondev.registry.dto.FamilyRequest
import com.ralphmarondev.registry.dto.FamilyResponse
import com.ralphmarondev.registry.entity.Family

fun Family.toResponse() = FamilyResponse(
    id = id,
    code = code,
    name = name,
    blockNumber = blockNumber,
    barangay = barangay,
    city = city,
    province = province,
    landline = landline,
    householdNumber = householdNumber,
    householdType = householdType,
    housingOwnership = housingOwnership,
    registrationStatus = registrationStatus,
    createDate = createDate,
    updateDate = updateDate,
    isDeleted = isDeleted
)

fun FamilyRequest.toFamily() = Family(
    code = code,
    name = name,
    blockNumber = blockNumber,
    barangay = barangay,
    city = city,
    province = province,
    landline = landline,
    householdNumber = householdNumber,
    householdType = householdType,
    housingOwnership = housingOwnership,
    registrationStatus = registrationStatus
)