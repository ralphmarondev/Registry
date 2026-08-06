package com.ralphmarondev.registry.controller

import com.ralphmarondev.registry.dto.RoleRequest
import com.ralphmarondev.registry.dto.RoleResponse
import com.ralphmarondev.registry.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}