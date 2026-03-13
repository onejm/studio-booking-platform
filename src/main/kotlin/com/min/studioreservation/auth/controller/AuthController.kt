package com.min.studioreservation.auth.controller

import com.min.studioreservation.auth.dto.LoginRequest
import com.min.studioreservation.auth.dto.LoginResponse
import com.min.studioreservation.auth.dto.SignUpRequest
import com.min.studioreservation.auth.dto.SignUpResponse
import com.min.studioreservation.auth.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@Valid @RequestBody request: SignUpRequest): SignUpResponse {
        return authService.signUp(request)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest
    ): LoginResponse {
        val response = authService.login(request)
        httpRequest.session
        return response
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(
        authentication: Authentication?,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ) {
        SecurityContextLogoutHandler().logout(httpRequest, httpResponse, authentication)
        SecurityContextHolder.clearContext()
    }
}
