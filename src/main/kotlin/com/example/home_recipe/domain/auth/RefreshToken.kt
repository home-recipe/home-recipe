package com.example.home_recipe.domain.auth

import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "refresh_token")
class RefreshToken (
    @Id
    val email: String,
    val token: String,
    val expiresAt: LocalDateTime
)