package com.min.studioreservation.auth.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.min.studioreservation.common.exception.ErrorResponse
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
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 정보가 유효하지 않습니다.")
                return
            }

            if (user.withdrawnAt != null) {
                logoutHandler.logout(request, response, authentication)
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "탈퇴한 회원입니다.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun writeError(response: HttpServletResponse, status: Int, message: String) {
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(ErrorResponse(message = message)))
    }
}
