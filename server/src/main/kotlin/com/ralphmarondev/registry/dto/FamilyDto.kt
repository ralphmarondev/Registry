package com.ralphmarondev.registry.dto

import java.time.LocalDateTime

data class FamilyRequest(
    val code: String,
    val name: String,
    val blockNumber: String,
    val barangay: String,
    val city: String,
    val province: String,
    val landline: String? = null
)

data class FamilyResponse(
    val id: Long,
    val code: String,
    val name: String,
    val blockNumber: String,
    val barangay: String,
    val city: String,
    val province: String,
    val landline: String? = null,
    val memberCount: Int = 0,
    val isDeleted: Boolean = false,
    val createDate: LocalDateTime,
    val updateDate: LocalDateTime?
)