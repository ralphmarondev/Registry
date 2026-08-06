package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.config.CurrentUserProvider
import com.ralphmarondev.registry.config.JwtService
import com.ralphmarondev.registry.dto.*
import com.ralphmarondev.registry.entity.Account
import com.ralphmarondev.registry.mapper.toAccountResponse
import com.ralphmarondev.registry.mapper.toRegisterResponse
import com.ralphmarondev.registry.repository.AccountRepository
import com.ralphmarondev.registry.repository.MemberRepository
import com.ralphmarondev.registry.repository.RoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountService(
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val currentUserProvider: CurrentUserProvider
) {
    fun register(request: RegisterRequest): RegisterResponse {
        if (accountRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("Username already taken.")
        }
        val role = roleRepository.findById(request.roleId)
            .orElseThrow { IllegalArgumentException("Role not found.") }

        val member = request.memberId?.let {
            memberRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Member not found.") }
        }

        val account = Account(
            username = request.username,
            password = passwordEncoder.encode(request.password) ?: "",
            email = request.email,
            role = role,
            member = member
        )
        val saved = accountRepository.save(account)
        return saved.toRegisterResponse()
    }

    fun login(request: LoginRequest): LoginResponse {
        val account = accountRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(request.password, account.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        val accessToken = jwtService.generateAccessToken(
            userId = account.id,
            username = account.username
        )
        val refreshToken = jwtService.generateRefreshToken(
            userId = account.id,
            username = account.username
        )
        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            account = account.toAccountResponse()
        )
    }

    fun me(): AccountResponse {
        val accountId = currentUserProvider.getCurrentUserId()
        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Account not found.") }

        return account.toAccountResponse()
    }
}