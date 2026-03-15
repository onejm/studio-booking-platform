package com.min.studioreservation.common.exception

import com.min.studioreservation.auth.exception.AlreadyWithdrawnException
import com.min.studioreservation.auth.exception.DuplicateEmailException
import com.min.studioreservation.auth.exception.UserNotFoundException
import com.min.studioreservation.auth.exception.WithdrawnUserException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateEmail(ex: DuplicateEmailException): ErrorResponse {
        return ErrorResponse(message = ex.message ?: "중복된 이메일입니다.")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: MethodArgumentNotValidException): ErrorResponse {
        val message = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "요청 값이 올바르지 않습니다."
        return ErrorResponse(message = message)
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentials(ex: BadCredentialsException): ErrorResponse {
        return ErrorResponse(message = ex.message ?: "이메일 또는 비밀번호가 올바르지 않습니다.")
    }

    @ExceptionHandler(WithdrawnUserException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleWithdrawnUser(ex: WithdrawnUserException): ErrorResponse {
        return ErrorResponse(message = ex.message ?: "탈퇴한 회원입니다.")
    }

    @ExceptionHandler(AlreadyWithdrawnException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyWithdrawn(ex: AlreadyWithdrawnException): ErrorResponse {
        return ErrorResponse(message = ex.message ?: "이미 탈퇴한 회원입니다.")
    }

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFound(ex: UserNotFoundException): ErrorResponse {
        return ErrorResponse(message = ex.message ?: "사용자를 찾을 수 없습니다.")
    }
}
