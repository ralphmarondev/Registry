package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.FamilyRequest
import com.ralphmarondev.registry.dto.FamilyResponse
import com.ralphmarondev.registry.service.FamilyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("family/")
class FamilyController(
    private val familyService: FamilyService
) {
    @PostMapping
    fun create(@RequestBody request: FamilyRequest): ResponseEntity<FamilyResponse> {
        val family = familyService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(family)
    }

    @GetMapping
    fun getAll(): List<FamilyResponse> {
        return familyService.getAll()
    }

    @GetMapping("{id}/")
    fun getById(@PathVariable id: Long): ResponseEntity<FamilyResponse> {
        val family = familyService.getById(id)
        return ResponseEntity.ok(family)
    }

    @GetMapping("code/{code}/")
    fun getByCode(@PathVariable code: String): ResponseEntity<FamilyResponse> {
        val family = familyService.getByCode(code)
        return ResponseEntity.ok(family)
    }

    @PutMapping("{id}/")
    fun update(@PathVariable id: Long, @RequestBody request: FamilyRequest): ResponseEntity<FamilyResponse> {
        val updated = familyService.update(id = id, request = request)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("{id}/")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        familyService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("batch/")
    fun batch(@RequestBody requests: List<FamilyRequest>): ResponseEntity<List<FamilyResponse>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(familyService.batch(requests))
    }
}