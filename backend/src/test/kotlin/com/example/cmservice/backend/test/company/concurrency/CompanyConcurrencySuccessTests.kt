package com.example.cmservice.backend.test.company.concurrency

import com.example.cmservice.backend.dto.toDto
import com.example.cmservice.backend.test.company.BaseCompanyTest
import com.example.cmservice.common.test.catchAnswerAndArgs
import com.example.cmservice.generated.client.model.CompanyCreateDto
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.any
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import com.example.cmservice.generated.client.model.CompanyDto as CompanyDtoClient
import com.example.cmservice.generated.server.model.CompanyDto as CompanyDtoServer

@TestPropertySource(
    properties = ["concurrency.failure.max.attempts=2"]
)
class CompanyConcurrencySuccessTests : BaseCompanyTest() {

    @Test
    fun `db locking occurs with successful retries - all return 200`() {
        val createdDto = companyApiClient.addCompany(CompanyCreateDto().name(javaClass.simpleName)).body!!

        val sleepTimeouts = ConcurrentLinkedQueue<Long>().also { it.add(0); it.add(50) }

        val updateInputArgs = ConcurrentLinkedQueue<CompanyDtoServer>()
        val updateAnswers = ConcurrentLinkedDeque<CompanyDtoServer>()
        val winner by lazy { updateAnswers.last }

        val barrier = CyclicBarrier(2)
        catchAnswerAndArgs { answer, args ->
            sleepTimeouts.poll()?.let { timeout ->
                barrier.await()
                TimeUnit.MILLISECONDS.sleep(timeout)
                updateInputArgs.add(args[0] as CompanyDtoServer)
                updateAnswers.add(answer as CompanyDtoServer)
            }
        }.whenever(companyService).updateCompany(any())

        val pool = Executors.newCachedThreadPool()
        val tasks = mutableSetOf<Future<*>>()
        repeat(2) { index ->
            tasks.add(
                pool.submit {
                    val requestDto = CompanyDtoClient().id(createdDto.id).name("${createdDto.name}-update$index")
                    val responseEntity = companyApiClient.updateCompany(requestDto)
                    val responseDto = responseEntity.body
                    assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
                    assertThat(responseDto)
                        .usingRecursiveComparison()
                        .withStrictTypeChecking()
                        .ignoringFields("created")
                        .isEqualTo(requestDto)
                }
            )
        }

        pool.shutdown()
        tasks.forEach { it.get(5, TimeUnit.SECONDS) }
        pool.awaitTermination(5, TimeUnit.SECONDS)

        assertThat(winner).isEqualTo(companyRepository.getReferenceById(createdDto.id).toDto())

        inOrder(companyController, companyService) {
            verify(companyController, times(1)).addCompany(any())
            verify(companyService, times(1)).addCompany(any())
            verify(companyController, times(3)).updateCompany(argWhere { updateInputArgs.contains(it) })
        }
        inOrder(companyController, companyService) {
            verify(companyController, times(1)).addCompany(any())
            verify(companyService, times(1)).addCompany(any())
            verify(companyService, times(3)).updateCompany(argWhere { updateInputArgs.contains(it) })
        }

        verifyNoMore()
    }
}
