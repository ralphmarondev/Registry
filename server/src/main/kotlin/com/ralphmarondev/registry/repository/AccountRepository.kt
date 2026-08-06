package com.ralphmarondev.registry.repository

import com.ralphmarondev.registry.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    fun findByUsername(username: String): Account?
}