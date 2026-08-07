package com.ralphmarondev.registry.entity

import com.ralphmarondev.registry.enums.HouseholdType
import com.ralphmarondev.registry.enums.HousingOwnership
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "families")
data class Family(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "code", unique = true)
    val code: String,
    val name: String,
    @Column(name = "block_number")
    val blockNumber: String,
    val barangay: String,
    val city: String,
    val province: String,
    val landline: String? = null,
    @Column(name = "household_number")
    val householdNumber: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "household_type")
    val householdType: HouseholdType,
    @Enumerated(EnumType.STRING)
    @Column(name = "housing_ownership")
    val housingOwnership: HousingOwnership,
    @Column(name = "registration_status")
    val registrationStatus: String,
    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,
    @Column(name = "create_date")
    val createDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "update_date")
    val updateDate: LocalDateTime? = null
)