package com.ralphmarondev.registry.dto

import java.time.LocalDateTime

data class BeneficiaryProgramRequest(
    val name: String,
    val description: String
)

data class BeneficiaryProgramResponse(
    val id: Long = 0,
    val name: String,
    val description: String,
    val isDeleted: Boolean = false,
    val createDate: LocalDateTime,
    val updateDate: LocalDateTime? = null
)