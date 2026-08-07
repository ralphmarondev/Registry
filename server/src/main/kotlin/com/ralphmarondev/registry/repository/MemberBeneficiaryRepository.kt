package com.ralphmarondev.registry.repository

import com.ralphmarondev.registry.entity.MemberBeneficiary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberBeneficiaryRepository : JpaRepository<MemberBeneficiary, Long> {
    fun finByMemberId(memberId: Long): List<MemberBeneficiary>
}