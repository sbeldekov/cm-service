package com.example.cmservice.backend.test.company.crud

import com.example.cmservice.backend.test.company.BaseCompanyTest
import com.example.cmservice.common.test.expectEntityNotFound
import com.example.cmservice.common.test.expectInvalidInput
import com.example.cmservice.common.test.isExactA
import com.example.cmservice.generated.client.model.CompanyCreateDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.get
import javax.validation.ConstraintViolationException
import kotlin.test.Ignore
import kotlin.test.Test

class CompanyGetTests : BaseCompanyTest() {

    private val createdDto by lazy { companyApiClient.addCompany(CompanyCreateDto().name(javaClass.simpleName)).body!! }

    @BeforeAll
    fun beforeAll() {
        companyApiClient.deleteCompanies()
        ::createdDto.invoke()
    }

    @Test
    fun `get company by id ok - 200`() {
        val responseEntity = companyApiClient.getCompanyById(createdDto.id)
        val responseDto = responseEntity.body

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseDto).isEqualTo(createdDto)

        inOrder(companyController, companyService) {
            verify(companyController).getCompanyById(eq(createdDto.id))
            verify(companyService).getCompanyById(eq(createdDto.id))
        }
        verifyNoMore()
    }

    @Test
    fun `get companies ok - 200`() {
        val responseEntity = companyApiClient.companies
        val responseDtos = responseEntity.body

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseDtos).containsExactly(createdDto)

        inOrder(companyController, companyService) {
            verify(companyController).companies
            verify(companyService).companies
        }
        verifyNoMore()
    }

    @Test
    @Ignore
    fun `company with invalid id - 400`() {
        mockMvc { get("/company/0").expectInvalidInput() }

        verify(exceptionHandler).invalidInput(isExactA<ConstraintViolationException>())
        verifyNoMore()
    }

    @Test
    fun `company not found - 204`() {
        mockMvc { get("/company/1000").expectEntityNotFound() }

        inOrder(companyController, companyService, exceptionHandler) {
            verify(companyController).getCompanyById(eq(1_000L))
            verify(companyService).getCompanyById(eq(1_000L))
            verify(exceptionHandler).entityNotFound(isExactA())
        }
        verifyNoMore()
    }
}
