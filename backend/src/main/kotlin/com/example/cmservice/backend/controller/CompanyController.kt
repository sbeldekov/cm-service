package com.example.cmservice.backend.controller

import com.example.cmservice.backend.service.CompanyService
import com.example.cmservice.generated.server.api.CompanyApi
import com.example.cmservice.generated.server.model.CompanyCreateDto
import com.example.cmservice.generated.server.model.CompanyDto
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.net.URI

@Controller
class CompanyController(
    private val companyService: CompanyService
) : CompanyApi, ConcurrencyFailureRecoverable {
    override fun addCompany(companyCreateDto: CompanyCreateDto) =
        companyService.addCompany(companyCreateDto)
            .let { companyDto ->
                ResponseEntity
                    .created(URI.create("/company/${companyDto.id}"))
                    .body(companyDto)
            }

    override fun updateCompany(companyDto: CompanyDto) =
        ResponseEntity.ok(companyService.updateCompany(companyDto))

    override fun getCompanyById(companyId: Long) =
        ResponseEntity.ok(companyService.getCompanyById(companyId))

    override fun getCompanies() =
        ResponseEntity.ok(companyService.companies)

    override fun deleteCompanyById(companyId: Long) =
        emptyResponse.also {
            companyService.deleteCompanyById(companyId)
        }

    override fun deleteCompanies() =
        emptyResponse.also {
            companyService.deleteCompanies()
        }
}

private val emptyResponse = ResponseEntity.ok().build<Void>()
