package com.example.home_recipe.controller.dto.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Email(message = "SIGNUP_ERROR_004")
    val email: String,

    @field:Size(
        min = 8,
        max = 20,
        message = "LOGIN_ERROR_003"
    ) val password: String
)
