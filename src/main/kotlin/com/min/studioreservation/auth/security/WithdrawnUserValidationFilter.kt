package com.min.studioreservation.auth.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.min.studioreservation.common.dto.ApiResponse
import com.min.studioreservation.common.exception.ErrorType
import com.min.studioreservation.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class WithdrawnUserValidationFilter(
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val logoutHandler = SecurityContextLogoutHandler()

    private val excludedPaths = setOf(
        "/api/auth/signup",
        "/api/auth/login",
        "/api/auth/logout"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val normalizedPath = request.servletPath.removeSuffix("/").ifEmpty { "/" }
        return normalizedPath in excludedPaths
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal

        if (principal is CustomUserPrincipal) {
            val user = userRepository.findById(principal.userId).orElse(null)
            if (user == null) {
                logoutHandler.logout(request, response, authentication)
                writeError(response, ErrorType.UNAUTHORIZED)
                return
            }

            if (user.withdrawnAt != null) {
                logoutHandler.logout(request, response, authentication)
                writeError(response, ErrorType.ACCOUNT_WITHDRAWN)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun writeError(response: HttpServletResponse, errorType: ErrorType) {
        response.status = errorType.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(ApiResponse.error<Any>(errorType)))
    }
}
