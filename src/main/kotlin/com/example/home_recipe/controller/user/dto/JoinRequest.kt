package com.example.home_recipe.controller.user.dto

data class JoinRequest(
    val name: String,
    val loginId: String,
    val password: String,
    val email: String,
    val phoneNumber: String
)
