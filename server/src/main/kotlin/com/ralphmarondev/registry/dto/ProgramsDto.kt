package com.ralphmarondev.registry.dto

import java.time.LocalDateTime

data class ProgramRequest(
    val name: String,
    val description: String
)

data class ProgramResponse(
    val id: Long = 0,
    val name: String,
    val description: String,
    val isDeleted: Boolean = false,
    val createDate: LocalDateTime,
    val updateDate: LocalDateTime? = null
)