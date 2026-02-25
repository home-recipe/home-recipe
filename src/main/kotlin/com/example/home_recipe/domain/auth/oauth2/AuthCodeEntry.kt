package com.example.home_recipe.domain.auth.oauth2

data class AuthCodeEntry(
    val challenge: String,
    val email: String,
)
