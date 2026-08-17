package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.RoleRequest
import com.ralphmarondev.registry.dto.RoleResponse
import com.ralphmarondev.registry.entity.Role
import com.ralphmarondev.registry.exception.ResourceAlreadyExistsException
import com.ralphmarondev.registry.exception.ResourceNotFoundException
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
            .orElseThrow { ResourceNotFoundException("Role not found.") }
            .toResponse()
    }

    fun getByName(name: String): RoleResponse {
        return roleRepository.findByName(name)
            ?.toResponse()
            ?: throw ResourceNotFoundException("Role '$name' not found.")
    }

    fun create(request: RoleRequest): RoleResponse {
        if (roleRepository.findByName(request.name) != null) {
            throw ResourceAlreadyExistsException("Role already exists.")
        }
        val role = Role(name = request.name)
        logger.info("Saving new role: ${request.name}")
        return roleRepository.save(role).toResponse()
    }

    fun update(id: Long, request: RoleRequest): RoleResponse {
        val role = roleRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Role not found.") }
        if (role.name != request.name && roleRepository.findByName(request.name) != null) {
            throw ResourceAlreadyExistsException("Role '${request.name}' already exists.")
        }
        val updatedRole = role.copy(
            name = request.name,
            updateDate = LocalDateTime.now()
        )
        logger.info("Updating role: ${updatedRole.name}")
        return roleRepository.save(updatedRole).toResponse()
    }

    fun delete(id: Long): RoleResponse {
        val role = roleRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Role wit id $id not found.") }
        val deletedRole = role.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )
        logger.info("Deleting role: ${deletedRole.name}")
        return roleRepository.save(deletedRole).toResponse()
    }

    fun batch(requests: List<RoleRequest>): List<RoleResponse> {
        return requests.map { create(it) }
    }
}