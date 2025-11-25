package com.example.home_recipe.controller.refrigerator.dto

data class UserJoinedEvent(
    val userId: Long,
    val email: String
)