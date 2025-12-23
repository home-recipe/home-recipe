package com.example.home_recipe.controller.auth.dto.request

import com.example.home_recipe.domain.auth.RefreshToken
import jakarta.validation.constraints.NotNull

data class RefreshTokenRequest(
    @NotNull
    val refreshToken: String
)
