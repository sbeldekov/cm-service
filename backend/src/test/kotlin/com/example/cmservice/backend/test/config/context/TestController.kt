package com.example.cmservice.backend.test.config.context

import com.example.cmservice.backend.exception.EntityNotFoundException
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class TestController : TestApi {

    override fun getOk(id: Long, type: Long) = ResponseEntity.ok(id)
    override fun postOk(testBody: TestBody) = ResponseEntity.ok(testBody)

    override fun getThrowUnsupportedOperationException() = throw UnsupportedOperationException()
    override fun getThrowConcurrencyFailureException() = throw ConcurrencyFailureException("")
    override fun getThrowDataIntegrityViolationException() = throw DataIntegrityViolationException("")
    override fun getThrowEntityNotFoundException() = throw EntityNotFoundException(Any::class)
}
