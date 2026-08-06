package com.ralphmarondev.registry.mapper

import com.ralphmarondev.registry.dto.AccountResponse
import com.ralphmarondev.registry.dto.RegisterResponse
import com.ralphmarondev.registry.entity.Account

fun Account.toRegisterResponse() = RegisterResponse(
    account = AccountResponse(
        id = id,
        username = username,
        email = email,
        role = role.toResponse()
    )
)

fun Account.toAccountResponse() = AccountResponse(
    id = id,
    username = username,
    email = email,
    role = role.toResponse()
)