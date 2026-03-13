package com.min.studioreservation.auth.security

import com.min.studioreservation.user.domain.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserPrincipal(
    val userId: Long,
    val name: String,
    val email: String,
    private val encodedPassword: String,
    val role: UserRole
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getPassword(): String = encodedPassword

    override fun getUsername(): String = email
}
