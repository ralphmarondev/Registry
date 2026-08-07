package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.FamilyRequest
import com.ralphmarondev.registry.dto.FamilyResponse
import com.ralphmarondev.registry.service.FamilyService
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
        return ResponseEntity.status(201).body(family)
    }

    @GetMapping
    fun getAll(): List<FamilyResponse> {
        return familyService.getAll()
    }

    @GetMapping("{code}/")
    fun getByCode(@PathVariable code: String): ResponseEntity<FamilyResponse> {
        val family = familyService.getByCode(code)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(family)
    }

    @DeleteMapping("{id}/")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        familyService.delete(id)
        return ResponseEntity.noContent().build()
    }
}