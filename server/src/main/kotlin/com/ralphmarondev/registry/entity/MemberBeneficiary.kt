package com.ralphmarondev.registry.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_beneficiary")
data class MemberBeneficiary(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id")
    val beneficiaryProgram: BeneficiaryProgram,

    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,
    @Column(name = "create_date")
    val createDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "update_date")
    val updateDate: LocalDateTime? = null
)