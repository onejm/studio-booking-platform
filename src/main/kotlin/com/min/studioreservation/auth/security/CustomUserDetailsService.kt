package com.min.studioreservation.auth.security

import com.min.studioreservation.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")

        return CustomUserPrincipal(
            userId = user.id ?: error("회원 ID가 존재하지 않습니다."),
            name = user.name,
            email = user.email,
            encodedPassword = user.password,
            role = user.role
        )
    }
}
