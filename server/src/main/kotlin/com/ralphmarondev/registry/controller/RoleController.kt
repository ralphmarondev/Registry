package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.RoleRequest
import com.ralphmarondev.registry.dto.RoleResponse
import com.ralphmarondev.registry.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("role/")
class RoleController(
    private val roleService: RoleService
) {
    @PostMapping
    fun create(@RequestBody request: RoleRequest): ResponseEntity<RoleResponse> {
        val role = roleService.create(request)
        return ResponseEntity.status(201).body(role)
    }

    @GetMapping
    fun getAll(): List<RoleResponse> {
        return roleService.getAll()
    }

    @PostMapping("register/batch/")
    fun batch(@RequestBody requests: List<RoleRequest>): ResponseEntity<List<RoleResponse>> {
        return ResponseEntity.status(201).body(roleService.batch(requests))
    }
}