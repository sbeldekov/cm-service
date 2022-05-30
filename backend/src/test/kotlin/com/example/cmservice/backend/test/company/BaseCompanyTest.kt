package com.example.cmservice.backend.test.company

import com.example.cmservice.backend.controller.CompanyController
import com.example.cmservice.backend.repo.CompanyRepository
import com.example.cmservice.backend.service.CompanyService
import com.example.cmservice.backend.test.BaseMvcTest
import com.example.cmservice.backend.test.config.context.company.CompanyApiClient
import com.example.cmservice.generated.client.model.CompanyCreateDto
import com.example.cmservice.generated.client.model.CompanyDto
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import java.net.URI
import java.time.OffsetDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

abstract class BaseCompanyTest : BaseMvcTest() {

    private val createDtosPrivate = mutableSetOf<CompanyCreateDto>()
    private val createdDtosPrivate = mutableSetOf<CompanyDto>()

    final val createDtos: Set<CompanyCreateDto>
        get() = createDtosPrivate

    final val createdDtos: Set<CompanyDto>
        get() = createdDtosPrivate

    @SpyBean
    final lateinit var companyController: CompanyController; private set

    @SpyBean
    final lateinit var companyService: CompanyService; private set

    @Autowired
    final lateinit var companyApiClient: CompanyApiClient; private set

    @Autowired
    final lateinit var companyRepository: CompanyRepository; private set

    final fun addCompanyOk(companyCreateDto: CompanyCreateDto) = assertNotNull(
        companyApiClient.addCompany(companyCreateDto)
    ) { responseEntity ->
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CREATED)
        assertNotNull(responseEntity.body) { companyDto ->
            assertThat(companyDto).usingRecursiveComparison().ignoringFields("id", "created")
                .isEqualTo(companyCreateDto)

            assertThat(companyDto.id).isPositive
            assertThat(companyDto.created).isBefore(OffsetDateTime.now())

            assertThat(responseEntity.headers.location).isEqualTo(URI.create("/company/${companyDto.id}"))

            createDtosPrivate.add(companyCreateDto)
            createdDtosPrivate.add(companyDto)
        }
    }

    @AfterTest
    @BeforeTest
    final fun clearCompanyInvocations() = clearInvocations(companyController, companyService)

    final override fun verifyNoMore() {
        super.verifyNoMore()
        verifyNoMoreInteractions(companyController, companyService)
    }
}
