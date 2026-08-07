package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.*
import com.ralphmarondev.registry.service.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("account/")
class AccountController(
    private val accountService: AccountService
) {
    @PostMapping("register/")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val user = accountService.register(request)
        return ResponseEntity.status(201).body(user)
    }

    @PostMapping("login/")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = accountService.login(request)
        return ResponseEntity.status(200).body(response)
    }

    @GetMapping("me/")
    fun me(): ResponseEntity<AccountResponse> {
        return ResponseEntity.ok(accountService.me())
    }

    @PostMapping("register/batch/")
    fun batch(
        @RequestBody requests: List<RegisterRequest>
    ): ResponseEntity<List<RegisterResponse>> {
        return ResponseEntity.status(201).body(accountService.batch(requests))
    }
}