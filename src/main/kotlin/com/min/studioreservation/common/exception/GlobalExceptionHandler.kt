package com.min.studioreservation.common.exception

import com.min.studioreservation.common.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    @Value("\${spring.profiles.active:local}")
    private val activeProfile: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val isDevMode: Boolean
        get() = activeProfile.split(",").map { it.trim() }.any { it == "local" || it == "dev" }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ApiResponse<*>> {
        val errorType = ex.errorType
        logger.warn("CustomException: {} - {}", errorType.code, ex.message)
        val detail = if (isDevMode) extractErrorDetail(ex) else null
        return ResponseEntity
            .status(errorType.status)
            .body(ApiResponse.error(errorType, detail = detail, data = ex.data))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<*>> {
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        logger.warn("ValidationException: {}", message)
        return ResponseEntity
            .status(ErrorType.INVALID_INPUT.status)
            .body(ApiResponse.error<Any>(ErrorType.INVALID_INPUT, detail = message))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(): ResponseEntity<ApiResponse<*>> {
        logger.warn("BadCredentialsException")
        return ResponseEntity
            .status(ErrorType.INVALID_CREDENTIALS.status)
            .body(ApiResponse.error<Any>(ErrorType.INVALID_CREDENTIALS))
    }

    @ExceptionHandler(PropertyReferenceException::class)
    fun handlePropertyReference(ex: PropertyReferenceException): ResponseEntity<ApiResponse<*>> {
        val detail = "'${ex.propertyName}' 필드는 ${ex.type.type.simpleName}에서 지원하지 않습니다."
        logger.warn("PropertyReferenceException: {}", detail)
        return ResponseEntity
            .status(ErrorType.INVALID_SORT_PROPERTY.status)
            .body(ApiResponse.error<Any>(ErrorType.INVALID_SORT_PROPERTY, detail = detail))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnhandled(ex: Exception): ResponseEntity<ApiResponse<*>> {
        logger.error("Unhandled exception", ex)
        val detail = if (isDevMode) extractErrorDetail(ex) else null
        return ResponseEntity
            .status(ErrorType.INTERNAL_SERVER_ERROR.status)
            .body(ApiResponse.error<Any>(ErrorType.INTERNAL_SERVER_ERROR, detail = detail))
    }

    private fun extractErrorDetail(ex: Exception): String {
        val rootCause = generateSequence(ex as Throwable) { it.cause }.last()
        val location = ex.stackTrace
            .firstOrNull { it.className.startsWith("com.min.studioreservation") }
            ?.let { " at ${it.className}.${it.methodName}:${it.lineNumber}" }
            ?: ""
        return "${rootCause.javaClass.simpleName}: ${rootCause.message}$location"
    }
}
