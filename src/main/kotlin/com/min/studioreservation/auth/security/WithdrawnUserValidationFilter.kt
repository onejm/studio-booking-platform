package com.min.studioreservation.auth.security

import com.min.studioreservation.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class WithdrawnUserValidationFilter(
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
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
                SecurityContextLogoutHandler().logout(request, response, authentication)
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json;charset=UTF-8"
                response.writer.write("""{"message":"인증 정보가 유효하지 않습니다."}""")
                return
            }

            if (user.withdrawnAt != null) {
                SecurityContextLogoutHandler().logout(request, response, authentication)
                response.status = HttpServletResponse.SC_FORBIDDEN
                response.contentType = "application/json;charset=UTF-8"
                response.writer.write("""{"message":"탈퇴한 회원입니다."}""")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
