package com.example.home_recipe.controller.auth.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)
