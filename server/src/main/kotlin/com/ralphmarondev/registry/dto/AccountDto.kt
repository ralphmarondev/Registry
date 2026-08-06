package com.ralphmarondev.registry.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val account: AccountResponse
)

data class AccountResponse(
    val id: Long,
    val username: String,
    val email: String?,
    val role: RoleResponse
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String? = null,
    val roleId: Long,
    val memberId: Long? = null
)

data class RegisterResponse(
    val account: AccountResponse
)