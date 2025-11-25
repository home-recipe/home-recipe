package com.example.home_recipe.controller.auth.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
