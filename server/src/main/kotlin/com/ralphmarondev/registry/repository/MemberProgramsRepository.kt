package com.ralphmarondev.registry.repository

import com.ralphmarondev.registry.entity.MemberProgram
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberProgramsRepository : JpaRepository<MemberProgram, Long> {
    fun findByMemberId(memberId: Long): List<MemberProgram>
    fun deleteAllByMemberId(memberId: Long)
}