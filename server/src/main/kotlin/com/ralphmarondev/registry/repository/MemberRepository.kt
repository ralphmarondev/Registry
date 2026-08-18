package com.ralphmarondev.registry.repository

import com.ralphmarondev.registry.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByFamilyId(familyId: Long): List<Member>
    fun countByFamilyId(familyId: Long): Int
}