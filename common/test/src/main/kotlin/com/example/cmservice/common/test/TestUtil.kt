package com.example.cmservice.common.test

import org.assertj.core.api.SoftAssertions
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.startsWith
import org.mockito.ArgumentMatcher
import org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress
import org.mockito.internal.util.Primitives.defaultValue
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.internal.createInstance
import org.mockito.kotlin.isA
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.verification.VerificationMode
import org.springframework.test.web.servlet.ResultActionsDsl

// https://www.baeldung.com/kotlin/mockmvc-kotlin-dsl

fun ResultActionsDsl.expectDataConflict() = andExpect {
    status { isConflict() }
    content { startsWith("Data conflict") }
}

fun ResultActionsDsl.expectEntityNotFound() = andExpect {
    status { isNoContent() }
    content { emptyString() }
}

fun ResultActionsDsl.expectUnhandled() = andExpect {
    status { isBadRequest() }
    content { startsWith("Unhandled exception") }
}

fun ResultActionsDsl.expectInvalidInput() = andExpect {
    status { isBadRequest() }
    content { startsWith("Invalid input") }
}

fun ResultActionsDsl.expectInvalidInputWithContent(content: String) = andExpect {
    status { isBadRequest() }
    content { equalTo(content) }
}

fun ResultActionsDsl.expectNotFoundWithContent(content: String) = andExpect {
    status { isNotFound() }
    content { equalTo(content) }
}

fun ResultActionsDsl.expectOkWithContent(content: String) = andExpect {
    status { isOk() }
    content { contentType("application/json") }
    content { json(content, true) }
}

fun assertSoftly(block: SoftAssertions.() -> Unit) = SoftAssertions.assertSoftly(block)

inline fun catchAnswerAndArgs(crossinline block: (Any?, Array<Any?>) -> Unit) = doAnswer { invocationOnMock ->
    invocationOnMock.callRealMethod().also { answer -> block(answer, invocationOnMock.arguments) }
}

inline fun <T : Any> verifyStrict(mock: T, mode: VerificationMode, block: T.() -> Unit) {
    block(verify(mock, mode))
    verifyNoMoreInteractions(mock)
}

inline fun <T : Any> verifyStrict(mock: T, block: T.() -> Unit) = verifyStrict(mock, times(1), block)

/**
 * Checks exact class type match.
 * @see [isA]
 */
inline fun <reified T : Any> isExactA(): T {
    mockingProgress().argumentMatcherStorage.reportMatcher(ExactInstanceOf(T::class.java))
    return defaultValue(T::class.java) ?: createInstance()
}

class ExactInstanceOf(private val clazz: Class<*>) : ArgumentMatcher<Any> {
    override fun matches(argument: Any) = (clazz === argument.javaClass)
    override fun toString() = "isExactA(${clazz.canonicalName})"
}
