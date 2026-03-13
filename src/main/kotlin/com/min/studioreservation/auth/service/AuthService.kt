package com.min.studioreservation.auth.service

import com.min.studioreservation.auth.dto.SignUpRequest
import com.min.studioreservation.auth.dto.SignUpResponse
import com.min.studioreservation.auth.exception.DuplicateEmailException
import com.min.studioreservation.user.domain.User
import com.min.studioreservation.user.domain.UserRole
import com.min.studioreservation.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}
