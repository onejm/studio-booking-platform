package com.min.studioreservation.auth.service

import com.min.studioreservation.auth.dto.LoginRequest
import com.min.studioreservation.auth.dto.LoginResponse
import com.min.studioreservation.auth.dto.SignUpRequest
import com.min.studioreservation.auth.dto.SignUpResponse
import com.min.studioreservation.auth.dto.WithdrawResponse
import com.min.studioreservation.auth.exception.AlreadyWithdrawnException
import com.min.studioreservation.auth.security.CustomUserPrincipal
import com.min.studioreservation.auth.exception.DuplicateEmailException
import com.min.studioreservation.auth.exception.UserNotFoundException
import com.min.studioreservation.auth.exception.WithdrawnUserException
import com.min.studioreservation.user.domain.User
import com.min.studioreservation.user.domain.UserRole
import com.min.studioreservation.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val sessionRegistry: SessionRegistry
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

        val principal = authentication.principal as CustomUserPrincipal
        val user = userRepository.findById(principal.userId)
            .orElseThrow { BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.") }
        if (user.withdrawnAt != null) {
            throw WithdrawnUserException("탈퇴한 회원입니다.")
        }

        SecurityContextHolder.getContext().authentication = authentication

        return LoginResponse(
            userId = principal.userId,
            name = principal.name,
            email = principal.email,
            role = principal.role
        )
    }

    fun registerSession(userId: Long, sessionId: String) {
        sessionRegistry.removeSessionInformation(sessionId)
        sessionRegistry.registerNewSession(sessionId, userId)
    }

    @Transactional
    fun withdraw(userId: Long): WithdrawResponse {
        val withdrawnAt = LocalDateTime.now()
        val updatedRows = userRepository.markWithdrawnIfActive(userId, withdrawnAt)
        if (updatedRows == 0) {
            if (!userRepository.existsById(userId)) {
                throw UserNotFoundException("사용자를 찾을 수 없습니다.")
            }
            throw AlreadyWithdrawnException("이미 탈퇴한 회원입니다.")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("사용자를 찾을 수 없습니다.") }
        sessionRegistry.getAllSessions(userId, false).forEach { it.expireNow() }

        return WithdrawResponse(
            userId = user.id ?: error("회원 ID가 존재하지 않습니다."),
            email = user.email,
            withdrawnAt = user.withdrawnAt ?: withdrawnAt
        )
    }
}
