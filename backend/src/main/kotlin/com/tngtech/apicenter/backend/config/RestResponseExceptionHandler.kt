package com.tngtech.apicenter.backend.config

import com.tngtech.apicenter.backend.domain.exceptions.SpecificationNotFoundException
import com.tngtech.apicenter.backend.domain.exceptions.SpecificationParseException
import com.tngtech.apicenter.backend.domain.exceptions.VersionAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant
import java.time.format.DateTimeFormatter
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

data class ErrorMessage(
        val httpReasonPhrase: String,
        val userMessage: String,
        val timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
)

fun responseFactory(userMessage: String, status: HttpStatus): ResponseEntity<ErrorMessage> {
    return ResponseEntity(
        ErrorMessage(status.reasonPhrase, userMessage),
        null,
        status
    )
}

@ControllerAdvice
class RestResponseExceptionHandler {

    @ExceptionHandler(SpecificationNotFoundException::class)
    fun handleNotFound(exception: SpecificationNotFoundException): ResponseEntity<ErrorMessage> {
        logger.info("Specification ${exception.specificationId} ${exception.version} not found", exception)
        return responseFactory("Specification not found", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SpecificationParseException::class)
    fun handleBadRequest(exception: SpecificationParseException) =
            responseFactory(exception.userMessage, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(VersionAlreadyExistsException::class)
    fun handleVersionAlreadyExists(exception: VersionAlreadyExistsException) =
            responseFactory("A specification with the same version already exists for ${exception.specificationTitle}", HttpStatus.CONFLICT)
}
