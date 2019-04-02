package com.tngtech.apicenter.backend.config

import com.tngtech.apicenter.backend.domain.exceptions.*
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {  }

data class ErrorMessage(
        val httpReasonPhrase: String,
        val userMessage: String,
        val timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
)

fun makeResponseEntity(userMessage: String, status: HttpStatus): ResponseEntity<ErrorMessage> {
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
        logger.info("Specification ${exception.specificationId} ${exception.version} not found")
        return makeResponseEntity("Specification not found", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SpecificationParseException::class)
    fun handleBadRequest(exception: SpecificationParseException) =
            makeResponseEntity(exception.userMessage, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(VersionAlreadyExistsException::class)
    fun handleVersionAlreadyExists(exception: VersionAlreadyExistsException) =
            makeResponseEntity("A specification with the same version already exists for ${exception.specificationTitle}", HttpStatus.CONFLICT)

    @ExceptionHandler(InvalidSpecificationIdException::class)
    fun handleUnacceptableId(exception: InvalidSpecificationIdException) =
            makeResponseEntity("The API ID supplied (${exception.userDefinedId}) should only contain numbers, " +
                    "A-Z characters, underscores and hyphens", HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MismatchedSpecificationIdException::class)
    fun handleServiceIdMismatch(exception: MismatchedSpecificationIdException) =
            makeResponseEntity("The API ID used in the upload URL (${exception.urlPathId}) " +
                    "is not the same as the API ID used in the specification body (${exception.userDefinedId})", HttpStatus.BAD_REQUEST)

    @ExceptionHandler(IdNotCastableToLongException::class)
    fun handleUncastableId(exception: IdNotCastableToLongException) =
            makeResponseEntity("A specifications must have an ID that can be parsed to a Java Long type" +
                    "to use the permissions model", HttpStatus.BAD_REQUEST)

    @ExceptionHandler(AclPermissionDeniedException::class)
    fun handlePermissionDenied(exception: AclPermissionDeniedException) =
            makeResponseEntity("You don't have edit permission on specification ${exception.specificationId}", HttpStatus.FORBIDDEN)
}

