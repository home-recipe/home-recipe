package com.example.home_recipe.controller.dto.user.dto

data class JoinResponse(
    val name: String,
    val loginId: String,
    val email: String,
    val phoneNumber: String,
    val role: String
)
