package com.example.cmservice.backend.test.company.crud

import com.example.cmservice.backend.test.company.BaseCompanyTest
import com.example.cmservice.backend.test.company.CompanyInvalidPayloadProvider
import com.example.cmservice.common.test.isExactA
import com.example.cmservice.generated.client.model.CompanyCreateDto
import com.example.cmservice.generated.client.model.CompanyDto
import feign.FeignException.FeignClientException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.kotlin.check
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.verify
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CompanyPutTests : BaseCompanyTest() {

    private val fooDto by lazy { companyApiClient.addCompany(CompanyCreateDto().name("foo")).body!! }
    private val bazDto by lazy { companyApiClient.addCompany(CompanyCreateDto().name("baz")).body!! }

    @BeforeAll
    fun beforeAll() {
        companyApiClient.deleteCompanies()
        ::fooDto.invoke()
        ::bazDto.invoke()
    }

    @Test
    fun `update company ok - 200`() {
        val updateDto = CompanyDto().id(fooDto.id).name("foo-update")
        val responseEntity = companyApiClient.updateCompany(updateDto)
        val responseDto = responseEntity.body

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseDto)
            .usingRecursiveComparison()
            .withStrictTypeChecking()
            .ignoringFields("created")
            .isEqualTo(updateDto)
        assertThat(responseDto)
            .usingRecursiveComparison()
            .withStrictTypeChecking()
            .ignoringFields("name")
            .isEqualTo(fooDto)

        inOrder(companyController, companyService) {
            verify(companyController).updateCompany(check {
                assertThat(it).usingRecursiveComparison().isEqualTo(updateDto)
            })
            verify(companyService).updateCompany(check {
                assertThat(it).usingRecursiveComparison().isEqualTo(updateDto)
            })
        }
        verifyNoMore()
    }

    @Test
    fun `company exits - 409`() {
        val updateDto = CompanyDto().id(fooDto.id).name(bazDto.name)
        val e = assertFailsWith<FeignClientException> { companyApiClient.updateCompany(updateDto) }

        assertThat(HttpStatus.valueOf(e.status())).isEqualTo(HttpStatus.CONFLICT)
        assertThat(e.contentUTF8()).startsWith("Data conflict")

        inOrder(companyController, companyService, exceptionHandler) {
            verify(companyController).updateCompany(check {
                assertThat(it).usingRecursiveComparison().isEqualTo(updateDto)
            })
            verify(companyService).updateCompany(check {
                assertThat(it).usingRecursiveComparison().isEqualTo(updateDto)
            })
            verify(exceptionHandler).dataConflict(isExactA<DataIntegrityViolationException>())
        }
        verifyNoMore()
    }

    @Test
    fun `company not found - 204`() {
        val updateDto = CompanyDto().id(1000).name("update")
        val responseEntity = companyApiClient.updateCompany(updateDto)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        assertThat(responseEntity.body).isNull()

        inOrder(companyController, companyService, exceptionHandler) {
            verify(companyController).updateCompany(check {
                assertThat(it).usingRecursiveComparison().isEqualTo(updateDto)
            })
            verify(companyService).updateCompany(check {
                assertThat(it).usingRecursiveComparison().isEqualTo(updateDto)
            })
            verify(exceptionHandler).entityNotFound(isExactA())
        }
        verifyNoMore()
    }

    @ParameterizedTest
    @ArgumentsSource(CompanyInvalidPayloadProvider::class)
    fun `invalid company - 400`(updateDto: CompanyDto) {
        val e = assertFailsWith<FeignClientException> { companyApiClient.updateCompany(updateDto) }

        assertThat(HttpStatus.valueOf(e.status())).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(e.contentUTF8()).startsWith("Invalid input")

        verify(exceptionHandler).invalidInput(isExactA<MethodArgumentNotValidException>())
        verifyNoMore()
    }
}
