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
        // Initialize roles if they don't exist
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

        // Always check and create admin account if it doesn't exist
        if (accountRepository.findByUsername("ralphmaron") == null) {
            println("Creating admin account...")
            val adminRole = roleRepository.findByName("Administrator")
                ?: throw IllegalStateException("Administrator role not found.")

            val password = passwordEncoder.encode("iscuteee")
                ?: throw IllegalStateException("Failed to encode password.")

            val adminAccount = Account(
                username = "ralphmaron",
                password = password,
                email = "ralphmaron@gmail.com",
                role = adminRole,
                member = null
            )
            accountRepository.save(adminAccount)
            println("Admin account created with username: ralphmaron")
        } else {
            println("Admin account already exists.")
        }
    }
}