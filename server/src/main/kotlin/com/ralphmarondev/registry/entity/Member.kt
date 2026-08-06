package com.ralphmarondev.registry.entity

import com.ralphmarondev.registry.enums.CivilStatus
import com.ralphmarondev.registry.enums.RelationshipToHead
import com.ralphmarondev.registry.enums.Sex
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "members")
data class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    val family: Family,
    @Column(name = "first_name")
    val firstName: String,
    @Column(name = "middle_name")
    val middleName: String,
    @Column(name = "last_name")
    val lastName: String,
    val suffix: String? = null,
    @Enumerated(EnumType.STRING)
    val sex: Sex,
    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate,
    @Column(name = "place_of_birth")
    val placeOfBirth: String,
    @Column(name = "phone_number")
    val phoneNumber: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "civil_status")
    val civilStatus: CivilStatus,
    val nationality: String,
    val religion: String,
    val occupation: String,
    @Column(name = "educational_attainment")
    val educationalAttainment: String,
    @Column(name = "is_head")
    val isHead: Boolean = false,
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_to_head")
    val relationshipToHead: RelationshipToHead,
    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,
    @Column(name = "create_date")
    val createDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "update_date")
    val updateDate: LocalDateTime? = null
)