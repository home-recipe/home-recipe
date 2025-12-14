package com.example.home_recipe.controller.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailRequest(
    @field:NotBlank(message = "SIGNUP_ERROR_001")
    @field:Email(message = "SIGNUP_ERROR_004")
    val email: String
)
