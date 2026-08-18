package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.config.CurrentUserProvider
import com.ralphmarondev.registry.config.JwtService
import com.ralphmarondev.registry.dto.*
import com.ralphmarondev.registry.entity.Account
import com.ralphmarondev.registry.exception.InvalidCredentialsException
import com.ralphmarondev.registry.exception.PasswordEncodingException
import com.ralphmarondev.registry.exception.ResourceAlreadyExistsException
import com.ralphmarondev.registry.exception.ResourceNotFoundException
import com.ralphmarondev.registry.mapper.toAccountResponse
import com.ralphmarondev.registry.mapper.toRegisterResponse
import com.ralphmarondev.registry.repository.AccountRepository
import com.ralphmarondev.registry.repository.MemberRepository
import com.ralphmarondev.registry.repository.RoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
            throw ResourceAlreadyExistsException("Username already taken.")
        }
        val role = roleRepository.findById(request.roleId)
            .orElseThrow { ResourceNotFoundException("Role with id ${request.roleId} not found.") }

        val member = request.memberId?.let {
            memberRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Member not found.") }
        }
        val password = passwordEncoder.encode(request.password)
            ?: throw PasswordEncodingException("Failed to encode password.")

        val account = Account(
            username = request.username,
            password = password,
            email = request.email,
            role = role,
            member = member
        )
        val saved = accountRepository.save(account)
        return saved.toRegisterResponse()
    }

    fun login(request: LoginRequest): LoginResponse {
        val account = accountRepository.findByUsername(request.username)
            ?.takeIf { !it.isDeleted }
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(request.password, account.password)) {
            throw InvalidCredentialsException()
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
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Account with id $accountId not found.") }

        return account.toAccountResponse()
    }

    fun update(id: Long, request: RegisterRequest): RegisterResponse {
        val account = accountRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Account with id $id not found.") }

        if (account.username != request.username && accountRepository.findByUsername(request.username) != null) {
            throw ResourceAlreadyExistsException("Username already taken.")
        }
        val role = roleRepository.findById(request.roleId)
            .orElseThrow {
                ResourceNotFoundException("Role with id ${request.roleId} not found.")
            }
        val member = request.memberId?.let {
            memberRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Member not found.") }
        }
        val password = passwordEncoder.encode(request.password)
            ?: throw PasswordEncodingException("Failed to encode password.")

        val updatedAccount = account.copy(
            username = request.username,
            password = password,
            email = request.email,
            role = role,
            member = member,
            updateDate = LocalDateTime.now()
        )
        return accountRepository.save(updatedAccount).toRegisterResponse()
    }

    fun delete(id: Long): RegisterResponse {
        val account = accountRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Account with id $id not found.") }
        val deletedAccount = account.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )
        return accountRepository.save(deletedAccount).toRegisterResponse()
    }

    fun batch(requests: List<RegisterRequest>): List<RegisterResponse> {
        return requests.map { register(it) }
    }
}