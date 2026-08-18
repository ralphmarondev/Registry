package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.ProgramRequest
import com.ralphmarondev.registry.dto.ProgramResponse
import com.ralphmarondev.registry.entity.Program
import com.ralphmarondev.registry.exception.ResourceAlreadyExistsException
import com.ralphmarondev.registry.exception.ResourceNotFoundException
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.ProgramRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ProgramService(
    private val programRepository: ProgramRepository
) {
    fun getAll(): List<ProgramResponse> {
        return programRepository.findAll()
            .filter { !it.isDeleted }
            .map { it.toResponse() }
    }

    fun getById(id: Long): ProgramResponse? {
        return programRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Program not found.") }
            .toResponse()
    }

    fun create(request: ProgramRequest): ProgramResponse {
        val existingProgram = programRepository.findByName(request.name)

        if (existingProgram != null && !existingProgram.isDeleted) {
            throw ResourceAlreadyExistsException("Program already exists")
        }
        val program = Program(
            name = request.name,
            description = request.description
        )
        return programRepository.save(program).toResponse()
    }

    fun update(id: Long, request: ProgramRequest): ProgramResponse {
        val program = programRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Program not found.") }
        val existingProgram = programRepository.findByName(request.name)

        if (program.name != request.name &&
            existingProgram != null &&
            !existingProgram.isDeleted
        ) {
            throw ResourceAlreadyExistsException("Program '${request.name}' already exists.")
        }
        val updatedProgram = program.copy(
            name = request.name,
            description = request.description,
            updateDate = LocalDateTime.now()
        )
        return programRepository.save(updatedProgram).toResponse()
    }

    fun delete(id: Long): ProgramResponse {
        val program = programRepository.findById(id)
            .filter { !it.isDeleted }
            .orElseThrow { ResourceNotFoundException("Program not found.") }
        val deletedProgram = program.copy(
            isDeleted = true,
            updateDate = LocalDateTime.now()
        )
        return programRepository.save(deletedProgram).toResponse()
    }

    fun batch(requests: List<ProgramRequest>): List<ProgramResponse> {
        return requests.map { create(it) }
    }
}