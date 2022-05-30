package com.example.cmservice.backend.exception

import mu.KotlinLogging
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import javax.validation.ConstraintViolationException

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class ExceptionHandler {

    // business exception: entity not found
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun entityNotFound(e: EntityNotFoundException) =
        logger.debug {
            "Entity not found: type=${e.type.simpleName}" +
                ", id=${e.id}".takeIf { e.id != null }.orEmpty()
        }

    @ExceptionHandler(
        // db lock failures
        ConcurrencyFailureException::class,
        // db constrains (unique, not null, etc)
        DataIntegrityViolationException::class
    )
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun dataConflict(e: DataAccessException) =
        "Data conflict: ${e.message}"
            .also { logger.debug { "exception: ${e.javaClass}: $it" } }

    @ExceptionHandler(
        // @PathVariable @RequestParam not valid (min, max, etc)
        ConstraintViolationException::class,
        // @RequestBody field not valid (min, max, etc)
        MethodArgumentNotValidException::class,
        // @PathVariable @RequestParam type mismatch
        MethodArgumentTypeMismatchException::class,
        // required @RequestParam missing
        MissingServletRequestParameterException::class,
        // required @RequestBody missing or malformed
        HttpMessageNotReadableException::class,
        // content-type not supported
        HttpMediaTypeNotSupportedException::class,
        // http method not supported
        HttpRequestMethodNotSupportedException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun invalidInput(e: Exception) =
        "Invalid input: ${e.message}"
            .also { logger.debug { "exception: ${e.javaClass}: $it" } }

    // web path not found, including required @PathVariable missing
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun notFound(e: NoHandlerFoundException) =
        "Not found: ${e.message}"
            .also { logger.debug { "exception: ${e.javaClass}: $it" } }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun unhandled(e: Exception) =
        "Unhandled exception: ${e.message}"
            .also { logger.error(e) { it } }
}
