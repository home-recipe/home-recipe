package com.example.home_recipe.controller.auth.dto.response

import com.example.home_recipe.domain.user.Role

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val role: Role
)
