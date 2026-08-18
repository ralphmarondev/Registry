package com.ralphmarondev.registry.mapper

import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.entity.Program

fun Program.toResponse() = ProgramResponse(
    id = id,
    name = name,
    description = description,
    isDeleted = isDeleted,
    createDate = createDate,
    updateDate = updateDate
)