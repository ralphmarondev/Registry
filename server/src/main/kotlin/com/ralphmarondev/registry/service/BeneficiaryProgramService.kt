package com.ralphmarondev.registry.service

import com.ralphmarondev.registry.dto.BeneficiaryProgramRequest
import com.ralphmarondev.registry.dto.BeneficiaryProgramResponse
import com.ralphmarondev.registry.entity.BeneficiaryProgram
import com.ralphmarondev.registry.mapper.toResponse
import com.ralphmarondev.registry.repository.BeneficiaryProgramRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BeneficiaryProgramService(
    private val beneficiaryRepository: BeneficiaryProgramRepository
) {
    fun getAll(): List<BeneficiaryProgramResponse> {
        return beneficiaryRepository.findAll()
            .filter { !it.isDeleted }
            .map { it.toResponse() }
    }

    fun create(request: BeneficiaryProgramRequest): BeneficiaryProgramResponse {
        if (beneficiaryRepository.findByName(request.name) != null) {
            throw RuntimeException("Beneficiary name already exists")
        }
        val program = BeneficiaryProgram(
            name = request.name,
            description = request.description
        )
        return beneficiaryRepository.save(program).toResponse()
    }

    fun batch(requests: List<BeneficiaryProgramRequest>): List<BeneficiaryProgramResponse> {
        return requests.map { create(it) }
    }
}