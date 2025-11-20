package com.example.home_recipe.controller.dto.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "LOGIN_ERROR_001")
    @field:Email(message = "SIGNUP_ERROR_004")
    val email: String,

    @field:NotBlank(message = "LOGIN_ERROR_001")
    @field:Size(
        min = 8,
        max = 20,
        message = "LOGIN_ERROR_003"
    ) val password: String
)
