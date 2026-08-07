package com.ralphmarondev.registry.repository

import com.ralphmarondev.registry.entity.BeneficiaryProgram
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BeneficiaryProgramRepository : JpaRepository<BeneficiaryProgram, Long> {
    fun findByName(name: String): BeneficiaryProgram?
}