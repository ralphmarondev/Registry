package com.ralphmarondev.registry.dto

data class RoleRequest(
    val name: String
)

data class RoleResponse(
    val id: Long = 0,
    val name: String,
    val isDeleted: Boolean = false
)