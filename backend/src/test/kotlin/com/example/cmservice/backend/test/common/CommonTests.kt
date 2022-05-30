package com.example.cmservice.backend.test.common

import com.example.cmservice.backend.test.BaseMvcTest
import com.example.cmservice.common.test.expectDataConflict
import com.example.cmservice.common.test.expectEntityNotFound
import com.example.cmservice.common.test.expectInvalidInput
import com.example.cmservice.common.test.expectInvalidInputWithContent
import com.example.cmservice.common.test.expectNotFoundWithContent
import com.example.cmservice.common.test.expectOkWithContent
import com.example.cmservice.common.test.expectUnhandled
import com.example.cmservice.common.test.isExactA
import com.example.cmservice.common.test.verifyStrict
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.validation.ConstraintViolationException
import kotlin.test.Test

class CommonTests : BaseMvcTest() {

    @Test
    fun `unhandled exception - return 400`() {
        mockMvc {
            get("/getThrowUnsupportedOperationException").expectUnhandled()
        }

        verifyStrict(exceptionHandler) { unhandled(isExactA<UnsupportedOperationException>()) }
    }

    @Test
    fun `ConcurrencyFailureException - return 409`() {
        mockMvc {
            get("/getThrowConcurrencyFailureException").expectDataConflict()
        }

        verifyStrict(exceptionHandler) { dataConflict(isExactA<ConcurrencyFailureException>()) }
    }

    @Test
    fun `DataIntegrityViolationException - return 409`() {
        mockMvc {
            get("/getThrowDataIntegrityViolationException").expectDataConflict()
        }

        verifyStrict(exceptionHandler) { dataConflict(isExactA<DataIntegrityViolationException>()) }
    }

    @Test
    fun `EntityNotFoundException - return 204`() {
        mockMvc {
            get("/getThrowEntityNotFoundException").expectEntityNotFound()
        }

        verifyStrict(exceptionHandler) { entityNotFound(isExactA()) }
    }

    @Test
    fun `web resource not found - return 404`() {
        mockMvc {
            get("/notfound").expectNotFoundWithContent(
                "Not found: No handler found for ${HttpMethod.GET} /notfound"
            )
        }

        verifyStrict(exceptionHandler) { notFound(isExactA()) }
    }

    @Test
    fun `no @PathVariable - return 404`() {
        mockMvc {
            get("/getOk") { param("type", "1") }.expectNotFoundWithContent(
                "Not found: No handler found for ${HttpMethod.GET} /getOk"
            )
        }

        verifyStrict(exceptionHandler) { notFound(isExactA()) }
    }

    @Test
    fun `no @RequestParam - return 400`() {
        mockMvc {
            get("/getOk/1").expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<MissingServletRequestParameterException>()) }
    }

    @Test
    fun `@PathVariable type mismatch - return 400`() {
        mockMvc {
            get("/getOk/test") { param("type", "1") }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<MethodArgumentTypeMismatchException>()) }
    }

    @Test
    fun `@RequestParam type mismatch - return 400`() {
        mockMvc {
            get("/getOk/1") { param("type", "test") }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<MethodArgumentTypeMismatchException>()) }
    }

    @Test
    fun `@PathVariable not valid - return 400`() {
        mockMvc {
            get("/getOk/0") { param("type", "1") }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<ConstraintViolationException>()) }
    }

    @Test
    fun `@RequestParam not valid - return 400`() {
        mockMvc {
            get("/getOk/1") { param("type", "0") }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<ConstraintViolationException>()) }
    }

    @Test
    fun `http request method not supported - return 400`() {
        mockMvc {
            post("/getOk/1") { param("type", "1") }.expectInvalidInputWithContent(
                "Invalid input: Request method '${HttpMethod.POST}' not supported"
            )
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<HttpRequestMethodNotSupportedException>()) }
    }

    @Test
    fun `content-type not supported - return 400`() {
        mockMvc {
            post("/postOk") {
                contentType = MediaType.TEXT_XML
                content = """{"body": "test"}"""
            }.expectInvalidInputWithContent("Invalid input: Content type '${MediaType.TEXT_XML}' not supported")
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<HttpMediaTypeNotSupportedException>()) }
    }

    @Test
    fun `@RequestBody not readable - return 400`() {
        mockMvc {
            post("/postOk") {
                contentType = MediaType.APPLICATION_JSON
                content = "{ 1: 1 }"
            }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<HttpMessageNotReadableException>()) }
    }

    @Test
    fun `no @RequestBody - return 400`() {
        mockMvc {
            post("/postOk") {
                contentType = MediaType.APPLICATION_JSON
            }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<HttpMessageNotReadableException>()) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["""{"body": ""}""", """{"body": null}""", "{}"]
    )
    fun `@RequestBody not valid - return 400`(input: String) {
        mockMvc {
            post("/postOk") {
                contentType = MediaType.APPLICATION_JSON
                content = input
            }.expectInvalidInput()
        }

        verifyStrict(exceptionHandler) { invalidInput(isExactA<MethodArgumentNotValidException>()) }
    }

    @Test
    fun `@RequestBody valid - return 200`() {
        mockMvc {
            post("/postOk") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"body": "test"}"""
            }.expectOkWithContent("""{"body": "test"}""")
        }

        verifyNoMoreInteractions(exceptionHandler)
    }
}
