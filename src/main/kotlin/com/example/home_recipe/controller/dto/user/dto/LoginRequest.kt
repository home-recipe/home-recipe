package com.example.home_recipe.controller.user.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)[a-z0-9]{5,20}\$",
        message = "LOGIN_ERROR_001"
    ) val loginId: String,

    @field:Size(
        min = 8,
        max = 20,
        message = "LOGIN_ERROR_003"
    ) val password: String
)
