package com.example.home_recipe.controller.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


data class JoinRequest(

    @field:NotBlank(message = "SIGNUP_ERROR_001")
    @field:Size(
        min = 2,
        max = 10,
        message = "SIGNUP_ERROR_002"
    ) val name: String,

    @field:NotBlank(message = "SIGNUP_ERROR_001")
    @field:Size(
        min = 8,
        max = 20,
        message = "SIGNUP_ERROR_003"
    ) val password: String,

    @field:NotBlank(message = "SIGNUP_ERROR_001")
    @field:Email(message = "SIGNUP_ERROR_004")
    val email: String,
)
