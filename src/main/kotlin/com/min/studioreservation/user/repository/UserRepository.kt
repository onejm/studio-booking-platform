package com.min.studioreservation.user.repository

import com.min.studioreservation.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        update User u
        set u.withdrawnAt = :withdrawnAt
        where u.id = :userId
          and u.withdrawnAt is null
        """
    )
    fun markWithdrawnIfActive(
        @Param("userId") userId: Long,
        @Param("withdrawnAt") withdrawnAt: LocalDateTime
    ): Int
}
