package com.example.cmservice.backend.test.company

import com.example.cmservice.generated.client.model.CompanyCreateDto
import feign.FeignException.FeignClientException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.data.history.RevisionMetadata
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CompanyPostTests : BaseCompanyTest() {

    private val createDto by lazy { createDtos.first() }

    @ParameterizedTest
    @ArgumentsSource(CompanyCreateSuccessProvider::class)
    @Order(1)
    fun `add new company - return 200`(
        companyCreateDto: CompanyCreateDto
    ) = addCompanyOk(companyCreateDto)

    @Test
    @Order(2)
    fun `add existing company - return 409`() =
        assertFailsWith<FeignClientException> {
            companyApiClient.addCompany(createDto)
        }.let { e ->
            assertThat(e.status()).isEqualTo(HttpStatus.CONFLICT.value())
            assertThat(e.contentUTF8()).startsWith("Data conflict")
            Unit
        }

    @ParameterizedTest
    @ArgumentsSource(CompanyCreateInvalidPayloadProvider::class)
    @Order(2)
    fun `add invalid company - return 400`(
        companyCreateDto: CompanyCreateDto
    ) =
        assertFailsWith<FeignClientException> {
            companyApiClient.addCompany(companyCreateDto)
        }.let { e ->
            assertThat(e.status()).isEqualTo(HttpStatus.BAD_REQUEST.value())
            assertThat(e.contentUTF8()).startsWith("Invalid input")
            Unit
        }

    @Test
    @Order(3)
    fun dataOk() =
        companyRepository.findAllById(createdDtos.map { it.id })
            .let { all ->
                createdDtos.forEach { createdDto ->
                    assertNotNull(all.singleOrNull { it.id == createdDto.id }) {
                        assertEquals(createdDto.name, it.name)
                        assertEquals(createdDto.created.toInstant(), it.createdDate)
                    }
                    assertNotNull(
                        companyRepository.findRevisions(createdDto.id)
                            .content.singleOrNull()
                    ) { revision ->
                        assertEquals(RevisionMetadata.RevisionType.INSERT, revision.metadata.revisionType)
                        assertEquals(createdDto.id, revision.entity.id)
                        assertEquals(createdDto.name, revision.entity.name)
                        assertNotNull(revision.requiredRevisionInstant)
                    }
                }
            }
}
