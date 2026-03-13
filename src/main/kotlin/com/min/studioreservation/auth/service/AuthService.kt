package com.min.studioreservation.auth.service

import com.min.studioreservation.auth.dto.LoginRequest
import com.min.studioreservation.auth.dto.LoginResponse
import com.min.studioreservation.auth.dto.SignUpRequest
import com.min.studioreservation.auth.dto.SignUpResponse
import com.min.studioreservation.auth.security.CustomUserPrincipal
import com.min.studioreservation.auth.exception.DuplicateEmailException
import com.min.studioreservation.user.domain.User
import com.min.studioreservation.user.domain.UserRole
import com.min.studioreservation.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {
    @Transactional
    fun signUp(request: SignUpRequest): SignUpResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateEmailException("이미 가입된 이메일입니다.")
        }

        val user = userRepository.save(
            User(
                name = request.name,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = UserRole.CUSTOMER
            )
        )

        return SignUpResponse(
            userId = user.id ?: error("회원 ID 생성에 실패했습니다."),
            name = user.name,
            email = user.email,
            role = user.role
        )
    }

    fun login(request: LoginRequest): LoginResponse {
        val authentication = try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (_: BadCredentialsException) {
            throw BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        SecurityContextHolder.getContext().authentication = authentication

        val principal = authentication.principal as CustomUserPrincipal
        return LoginResponse(
            userId = principal.userId,
            name = principal.name,
            email = principal.email,
            role = principal.role
        )
    }
}
