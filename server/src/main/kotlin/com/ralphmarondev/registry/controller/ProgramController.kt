package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.ProgramRequest
import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.service.ProgramService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("program/")
class ProgramController(
    private val beneficiaryProgramService: ProgramService
) {
    @PostMapping
    fun create(@RequestBody request: ProgramRequest): ResponseEntity<ProgramResponse> {
        val beneficiaryProgram = beneficiaryProgramService.create(request)
        return ResponseEntity.status(201).body(beneficiaryProgram)
    }

    @GetMapping
    fun getAll(): List<ProgramResponse> {
        return beneficiaryProgramService.getAll()
    }

    @PostMapping("batch/")
    fun batch(@RequestBody requests: List<ProgramRequest>): ResponseEntity<List<ProgramResponse>> {
        return ResponseEntity.status(201).body(beneficiaryProgramService.batch(requests))
    }
}