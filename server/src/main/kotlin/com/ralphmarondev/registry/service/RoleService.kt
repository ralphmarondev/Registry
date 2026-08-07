package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.RoleRequest
import com.ralphmarondev.registry.dto.RoleResponse
import com.ralphmarondev.registry.entity.Role
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RoleService(
    private val roleRepository: RoleRepository
) {
    private val logger = LoggerFactory.getLogger(RoleService::class.java)

    fun getAll(): List<RoleResponse> {
        return roleRepository.findAll().map {
            it.toResponse()
        }.filter {
            !it.isDeleted
        }
    }

    fun getById(id: Long): RoleResponse {
        return roleRepository.findById(id)
            .orElseThrow { RuntimeException("Role not found.") }
            .toResponse()
    }

    fun getByName(name: String): RoleResponse? {
        return roleRepository.findByName(name)?.toResponse()
    }

    fun create(request: RoleRequest): RoleResponse {
        if (roleRepository.findByName(request.name) != null) {
            throw RuntimeException("Role already exists.")
        }
        val role = Role(name = request.name)
        logger.info("Saving new role: ${request.name}")
        return roleRepository.save(role).toResponse()
    }

    fun batch(requests: List<RoleRequest>): List<RoleResponse> {
        return requests.map { create(it) }
    }
}