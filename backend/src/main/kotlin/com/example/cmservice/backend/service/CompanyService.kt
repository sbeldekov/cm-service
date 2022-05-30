package com.example.cmservice.backend.service

import com.example.cmservice.backend.dto.toDto
import com.example.cmservice.backend.dto.toEntity
import com.example.cmservice.backend.dto.update
import com.example.cmservice.backend.exception.EntityNotFoundException
import com.example.cmservice.backend.repo.Company
import com.example.cmservice.backend.repo.CompanyRepository
import com.example.cmservice.generated.server.model.CompanyCreateDto
import com.example.cmservice.generated.server.model.CompanyDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompanyService(
    private val companyRepository: CompanyRepository
) {
    @Transactional
    fun addCompany(companyCreateDto: CompanyCreateDto) =
        companyRepository.save(companyCreateDto.toEntity()).toDto()

    @Transactional
    fun updateCompany(companyDto: CompanyDto) =
        companyRepository.findById(companyDto.id)
            .orElseThrow { throw EntityNotFoundException(Company::class, companyDto.id) }
            .update(companyDto)
            .toDto()

    fun getCompanyById(companyId: Long) =
        companyRepository.findById(companyId)
            .orElseThrow { throw EntityNotFoundException(Company::class, companyId) }
            .toDto()

    val companies
        get() = companyRepository.findAll()
            .map { it.toDto() }
            .takeIf { it.isNotEmpty() }
            ?.toSet()
            ?: throw EntityNotFoundException(Company::class)

    @Transactional
    fun deleteCompanyById(companyId: Long) =
        companyRepository.delete(
            companyRepository.findById(companyId)
                .orElseThrow { throw EntityNotFoundException(Company::class, companyId) }
        )

    @Transactional
    fun deleteCompanies() =
        takeIf { companyRepository.count() > 0 }
            ?.let { companyRepository.deleteAll() }
            ?: throw EntityNotFoundException(Company::class)
}
