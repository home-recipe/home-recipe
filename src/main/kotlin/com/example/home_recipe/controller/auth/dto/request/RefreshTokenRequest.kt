package com.example.home_recipe.controller.auth.dto.request

import jakarta.validation.constraints.NotNull

data class RefreshTokenRequest(
    @NotNull
    val refreshToken: String
)
