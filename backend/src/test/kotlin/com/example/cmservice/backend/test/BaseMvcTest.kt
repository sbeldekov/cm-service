package com.example.cmservice.backend.test

import com.example.cmservice.backend.exception.ExceptionHandler
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@AutoConfigureMockMvc(
    printOnlyOnFailure = false
)
abstract class BaseMvcTest : BaseTest() {

    @SpyBean
    final lateinit var exceptionHandler: ExceptionHandler; private set

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    final lateinit var mockMvc: MockMvc; private set

    final inline fun mockMvc(block: MockMvc.() -> Unit) = block(mockMvc)

    @AfterTest
    @BeforeTest
    final fun clearExceptionHandlerInvocations() = clearInvocations(exceptionHandler)

    fun verifyNoMore() = verifyNoMoreInteractions(exceptionHandler)
}
