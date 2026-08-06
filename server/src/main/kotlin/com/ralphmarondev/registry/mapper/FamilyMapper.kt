package com.ralphmarondev.registry.mapper

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
    createDate = createDate,
    updateDate = updateDate,
    isDeleted = isDeleted
)