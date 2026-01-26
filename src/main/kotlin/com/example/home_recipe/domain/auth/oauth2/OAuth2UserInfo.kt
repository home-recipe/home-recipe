package com.example.home_recipe.domain.auth.oauth2

interface OAuth2UserInfo {
    val provider: OAuth2Provider
    val providerId: String
    val email: String
    val name: String
}