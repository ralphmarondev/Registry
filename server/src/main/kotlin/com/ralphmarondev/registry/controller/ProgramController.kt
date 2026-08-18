package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.ProgramRequest
import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.service.ProgramService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("program/")
class ProgramController(
    private val programService: ProgramService
) {
    @PostMapping
    fun create(@RequestBody request: ProgramRequest): ResponseEntity<ProgramResponse> {
        val program = programService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(program)
    }

    @GetMapping
    fun getAll(): List<ProgramResponse> {
        return programService.getAll()
    }

    @GetMapping("{id}/")
    fun getById(id: Long): ResponseEntity<ProgramResponse> {
        return ResponseEntity.ok().body(programService.getById(id))
    }

    @PutMapping("{id}/")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: ProgramRequest
    ): ResponseEntity<ProgramResponse> {
        return ResponseEntity.ok(programService.update(id, request))
    }

    @DeleteMapping("{id}/")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(programService.delete(id))
    }

    @PostMapping("batch/")
    fun batch(@RequestBody requests: List<ProgramRequest>): ResponseEntity<List<ProgramResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(programService.batch(requests))
    }
}