package com.ralphmarondev.registry.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "accounts")
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true)
    val username: String,
    val password: String,
    @Column(unique = true)
    val email: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    val role: Role,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = null,

    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,
    @Column(name = "create_date")
    val createDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "update_date")
    val updateDate: LocalDateTime? = null
)