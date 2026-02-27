package com.example.home_recipe.controller.auth.dto.response

import com.example.home_recipe.domain.user.Role
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String? = null,
    val role: Role
)
