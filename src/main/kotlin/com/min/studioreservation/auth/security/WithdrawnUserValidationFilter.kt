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

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path == "/api/auth/signup" || path == "/api/auth/login" || path == "/api/auth/logout"
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
            if (user?.withdrawnAt != null) {
                SecurityContextLogoutHandler().logout(request, response, authentication)
                SecurityContextHolder.clearContext()
                response.status = HttpServletResponse.SC_FORBIDDEN
                response.contentType = "application/json;charset=UTF-8"
                response.writer.write("""{"message":"탈퇴한 회원입니다."}""")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
