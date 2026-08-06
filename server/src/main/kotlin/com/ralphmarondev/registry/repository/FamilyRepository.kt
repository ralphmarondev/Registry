package com.ralphmarondev.registry.repository

import com.ralphmarondev.registry.entity.Family
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FamilyRepository : JpaRepository<Family, Long> {
    fun findByCode(familyCode: String): Family?
}