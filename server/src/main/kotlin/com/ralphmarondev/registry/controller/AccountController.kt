package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.*
import com.ralphmarondev.registry.service.AccountService
import org.springframework.http.HttpStatus
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
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @PostMapping("login/")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = accountService.login(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("me/")
    fun me(): ResponseEntity<AccountResponse> {
        return ResponseEntity.ok(accountService.me())
    }

    @PutMapping("{id}/")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody request: RegisterRequest
    ): ResponseEntity<RegisterResponse> {
        return ResponseEntity.ok(accountService.update(id, request))
    }

    @DeleteMapping("{id}/")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<RegisterResponse> {
        return ResponseEntity.ok(accountService.delete(id))
    }

    @PostMapping("batch/")
    fun batch(
        @RequestBody requests: List<RegisterRequest>
    ): ResponseEntity<List<RegisterResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.batch(requests))
    }
}