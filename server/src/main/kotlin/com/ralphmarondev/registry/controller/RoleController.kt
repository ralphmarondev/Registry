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
    @GetMapping
    fun getAll(): List<RoleResponse> {
        return roleService.getAll()
    }

    @GetMapping("{id}")
    fun getById(@PathVariable("id") id: Long): ResponseEntity<RoleResponse> {
        return ResponseEntity.ok(roleService.getById(id))
    }

    @GetMapping("name/{name}")
    fun getByName(@PathVariable("name") name: String): ResponseEntity<RoleResponse> {
        return ResponseEntity.ok(roleService.getByName(name))
    }

    @PostMapping
    fun create(@RequestBody request: RoleRequest): ResponseEntity<RoleResponse> {
        val role = roleService.create(request)
        return ResponseEntity.status(201).body(role)
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody request: RoleRequest
    ): ResponseEntity<RoleResponse> {
        return ResponseEntity.ok(roleService.update(id, request))
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<RoleResponse> {
        return ResponseEntity.ok(roleService.delete(id))
    }

    @PostMapping("register/batch/")
    fun batch(@RequestBody requests: List<RoleRequest>): ResponseEntity<List<RoleResponse>> {
        return ResponseEntity.status(201).body(roleService.batch(requests))
    }
}