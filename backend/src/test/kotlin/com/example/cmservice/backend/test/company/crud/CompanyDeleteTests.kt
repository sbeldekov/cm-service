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
import org.springframework.test.web.servlet.delete
import javax.validation.ConstraintViolationException
import kotlin.test.Ignore
import kotlin.test.Test

class CompanyDeleteTests : BaseCompanyTest() {

    @BeforeAll
    fun beforeAll() {
        companyApiClient.deleteCompanies()
    }

    @Test
    fun `delete company by id ok - 200`() {
        val createdDto = companyApiClient.addCompany(CompanyCreateDto().name("deleteCompanyById")).body!!
        clearCompanyInvocations()

        val responseEntity = companyApiClient.deleteCompanyById(createdDto.id)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNull()

        inOrder(companyController, companyService) {
            verify(companyController).deleteCompanyById(eq(createdDto.id))
            verify(companyService).deleteCompanyById(eq(createdDto.id))
        }
        verifyNoMore()
    }

    @Test
    fun `delete companies ok - 200`() {
        companyApiClient.addCompany(CompanyCreateDto().name("deleteCompanies"))
        clearCompanyInvocations()

        val responseEntity = companyApiClient.deleteCompanies()
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNull()

        inOrder(companyController, companyService) {
            verify(companyController).deleteCompanies()
            verify(companyService).deleteCompanies()
        }
        verifyNoMore()
    }

    @Test
    @Ignore
    fun `company with invalid id - 400`() {
        mockMvc { delete("/company/0").expectInvalidInput() }

        verify(exceptionHandler).invalidInput(isExactA<ConstraintViolationException>())
        verifyNoMore()
    }

    @Test
    fun `company not found - 204`() {
        mockMvc { delete("/company/1000").expectEntityNotFound() }

        inOrder(companyController, companyService, exceptionHandler) {
            verify(companyController).deleteCompanyById(eq(1_000L))
            verify(companyService).deleteCompanyById(eq(1_000L))
            verify(exceptionHandler).entityNotFound(isExactA())
        }
        verifyNoMore()
    }

    @Test
    fun `companies not found - 204`() {
        companyApiClient.deleteCompanies()
        clearCompanyInvocations()
        clearExceptionHandlerInvocations()

        mockMvc { delete("/company").expectEntityNotFound() }

        inOrder(companyController, companyService, exceptionHandler) {
            verify(companyController).deleteCompanies()
            verify(companyService).deleteCompanies()
            verify(exceptionHandler).entityNotFound(isExactA())
        }
        verifyNoMore()
    }
}
