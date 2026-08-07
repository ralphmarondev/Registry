package com.ralphmarondev.registry.mapper

import com.ralphmarondev.registry.dto.BeneficiaryProgramResponse
import com.ralphmarondev.registry.entity.BeneficiaryProgram

fun BeneficiaryProgram.toResponse() = BeneficiaryProgramResponse(
    id = id,
    name = name,
    description = description,
    isDeleted = isDeleted,
    createDate = createDate,
    updateDate = updateDate
)