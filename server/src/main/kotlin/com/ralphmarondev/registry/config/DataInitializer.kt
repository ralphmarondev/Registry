package com.ralphmarondev.registry.config

import com.ralphmarondev.registry.entity.Account
import com.ralphmarondev.registry.entity.Role
import com.ralphmarondev.registry.repository.AccountRepository
import com.ralphmarondev.registry.repository.RoleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String) {
        if (roleRepository.count() == 0L) {
            println("Initializing default roles...")
            val roles = listOf(
                Role(name = "Administrator"),
                Role(name = "Staff"),
                Role(name = "FamilyAdmin")
            )
            val savedRoles = roleRepository.saveAll(roles)
            println("Created: ${savedRoles.size} roles.")
        }

        if (accountRepository.findByUsername("admin") == null) {
            println("Creating admin account...")
            val adminRole = roleRepository.findByName("Administrator")
                ?: throw IllegalStateException("Administrator role not found.")

            val password = passwordEncoder.encode("adminnimda")
                ?: throw IllegalStateException("Failed to encode password.")

            val adminAccount = Account(
                username = "admin",
                password = password,
                role = adminRole
            )
            accountRepository.save(adminAccount)
            println("Admin account created with username: admin")
        } else {
            println("Admin account already exists.")
        }
    }
}