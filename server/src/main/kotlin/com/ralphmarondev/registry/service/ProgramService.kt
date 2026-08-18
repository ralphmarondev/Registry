package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.ProgramRequest
import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.entity.Program
import com.ralphmarondev.registry.exception.ResourceAlreadyExistsException
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.ProgramRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProgramService(
    private val beneficiaryRepository: ProgramRepository
) {
    fun getAll(): List<ProgramResponse> {
        return beneficiaryRepository.findAll()
            .filter { !it.isDeleted }
            .map { it.toResponse() }
    }

    fun create(request: ProgramRequest): ProgramResponse {
        if (beneficiaryRepository.findByName(request.name) != null) {
            throw ResourceAlreadyExistsException("Beneficiary name already exists")
        }
        val program = Program(
            name = request.name,
            description = request.description
        )
        return beneficiaryRepository.save(program).toResponse()
    }

    fun batch(requests: List<ProgramRequest>): List<ProgramResponse> {
        return requests.map { create(it) }
    }
}