package com.min.studioreservation.auth.dto

import java.time.LocalDateTime

data class WithdrawResponse(
    val userId: Long,
    val email: String,
    val withdrawnAt: LocalDateTime
)
