package com.example.cmservice.backend.test.company.audit

import com.example.cmservice.backend.test.company.BaseCompanyTest
import com.example.cmservice.generated.client.model.CompanyCreateDto
import org.assertj.core.api.Assertions.assertThat
import org.springframework.data.history.RevisionMetadata.RevisionType
import java.time.Instant
import kotlin.test.Test

class CompanyAuditTests : BaseCompanyTest() {

    @Test
    fun `envers audit data recorded`() {
        val start = Instant.now()
        val createdDto = companyApiClient.addCompany(CompanyCreateDto().name("envers")).body!!
        companyApiClient.updateCompany(createdDto.name("envers-update"))
        companyApiClient.deleteCompanyById(createdDto.id)
        val end = Instant.now()

        val revisions = companyRepository.findRevisions(createdDto.id)

        assertThat(revisions)
            .isNotNull
            .isNotEmpty
            .hasSize(3)
            .doesNotContainNull()
            .doesNotHaveDuplicates()

        listOf(RevisionType.INSERT, RevisionType.UPDATE, RevisionType.DELETE).forEach { revisionType ->
            assertThat(revisions).`as` { "check $revisionType revision type" }
                .filteredOn { it.metadata.revisionType == revisionType }
                .singleElement()
                .satisfies({
                    assertThat(it.metadata.requiredRevisionNumber).isPositive
                    assertThat(it.metadata.requiredRevisionInstant).isBetween(start, end)
                    assertThat(it.entity.id).isEqualTo(createdDto.id)
                    assertThat(it.entity.name).isEqualTo(
                        when (revisionType) {
                            RevisionType.INSERT -> "envers"
                            else -> "envers-update"
                        }
                    )
                    assertThat(it.entity.createdDate).isNull()
                    assertThat(it.entity.lastModifiedDate).isNull()
                    assertThat(it.entity.version).isNull()
                })
        }
    }

    @Test
    fun `jpa audit data recorded`() {
        val start = Instant.now()
        val createdDto = companyApiClient.addCompany(CompanyCreateDto().name("jpa")).body!!
        companyApiClient.updateCompany(createdDto.name("jpa-update"))
        val end = Instant.now()

        val company = companyRepository.getReferenceById(createdDto.id)
        assertThat(company.id).isEqualTo(createdDto.id)
        assertThat(company.name).isEqualTo("jpa-update")
        assertThat(company.createdDate).isBetween(start, end)
        assertThat(company.lastModifiedDate).isBetween(start, end).isAfterOrEqualTo(company.createdDate)
        assertThat(company.version).isPositive
    }
}
