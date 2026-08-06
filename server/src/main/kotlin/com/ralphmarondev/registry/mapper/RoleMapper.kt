package com.ralphmarondev.registry.mapper

import com.ralphmarondev.registry.dto.RoleResponse
import com.ralphmarondev.registry.entity.Role

fun Role.toResponse(): RoleResponse {
    return RoleResponse(
        id = id,
        name = name,
        isDeleted = isDeleted
    )
}