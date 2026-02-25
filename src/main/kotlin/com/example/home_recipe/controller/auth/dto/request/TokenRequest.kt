package com.example.home_recipe.controller.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class TokenRequest(
    @field:NotBlank
    val code: String,

    @field:NotBlank
    @JsonProperty("code_verifier")
    val codeVerifier: String,
)
