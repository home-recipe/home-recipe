package com.example.home_recipe.controller.user.dto

data class UserResponse(
    val name: String,
    val loginId: String,
    val email: String,
    val phoneNumber: String,
    val role: String
)
