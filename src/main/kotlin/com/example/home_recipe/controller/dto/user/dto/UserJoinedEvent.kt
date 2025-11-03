package com.example.home_recipe.controller.dto.user.dto

data class UserJoinedEvent(
    val userId: Long,
    val email: String
)
