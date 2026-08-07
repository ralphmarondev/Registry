package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.BeneficiaryProgramRequest
import com.ralphmarondev.registry.dto.BeneficiaryProgramResponse
import com.ralphmarondev.registry.service.BeneficiaryProgramService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("beneficiary-programs/")
class BeneficiaryProgramController(
    private val beneficiaryProgramService: BeneficiaryProgramService
) {
    @PostMapping
    fun create(@RequestBody request: BeneficiaryProgramRequest): ResponseEntity<BeneficiaryProgramResponse> {
        val beneficiaryProgram = beneficiaryProgramService.create(request)
        return ResponseEntity.status(201).body(beneficiaryProgram)
    }

    @GetMapping
    fun getAll(): List<BeneficiaryProgramResponse> {
        return beneficiaryProgramService.getAll()
    }
}