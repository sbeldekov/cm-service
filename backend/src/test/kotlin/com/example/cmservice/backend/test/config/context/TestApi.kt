package com.example.cmservice.backend.test.config.context

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid
import javax.validation.constraints.Min

@Validated
interface TestApi {

    @RequestMapping(
        method = [RequestMethod.GET],
        path = ["/getOk/{id}"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getOk(
        @PathVariable("id")
        @Min(1)
        id: Long,
        @RequestParam
        @Min(1)
        type: Long
    ): ResponseEntity<Long>

    @RequestMapping(
        method = [RequestMethod.POST],
        path = ["/postOk"],
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun postOk(
        @RequestBody
        @Valid
        testBody: TestBody
    ): ResponseEntity<TestBody>

    @RequestMapping(
        method = [RequestMethod.GET],
        path = ["/getThrowUnsupportedOperationException"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getThrowUnsupportedOperationException(): ResponseEntity<String>

    @RequestMapping(
        method = [RequestMethod.GET],
        path = ["/getThrowConcurrencyFailureException"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getThrowConcurrencyFailureException(): ResponseEntity<String>

    @RequestMapping(
        method = [RequestMethod.GET],
        path = ["/getThrowDataIntegrityViolationException"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getThrowDataIntegrityViolationException(): ResponseEntity<String>

    @RequestMapping(
        method = [RequestMethod.GET],
        path = ["/getThrowEntityNotFoundException"],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun getThrowEntityNotFoundException(): ResponseEntity<String>
}
