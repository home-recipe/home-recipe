package com.example.home_recipe.repository

import com.example.home_recipe.domain.auth.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByUser_Email(email: String): Optional<RefreshToken>

    fun findByRefreshToken(refreshToken: String) : Optional<RefreshToken>
}