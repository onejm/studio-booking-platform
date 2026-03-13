package com.min.studioreservation.auth.dto

import com.min.studioreservation.user.domain.UserRole

data class LoginResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val role: UserRole
)
