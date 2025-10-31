package com.example.home_recipe.controller.dto.auth.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)
