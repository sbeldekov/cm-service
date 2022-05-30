package com.example.cmservice.backend.dto

import com.example.cmservice.backend.repo.Company
import com.example.cmservice.generated.server.model.CompanyCreateDto
import com.example.cmservice.generated.server.model.CompanyDto
import java.time.ZoneOffset

fun CompanyCreateDto.toEntity() = Company(
    name = this.name
)

fun Company.toDto(): CompanyDto = CompanyDto()
    .id(this.id)
    .name(this.name)
    .created(this.createdDate?.atOffset(ZoneOffset.UTC))

fun Company.update(companyDto: CompanyDto) = this.also { company ->
    company.name = companyDto.name
}
