package com.example.home_recipe.controller.admin.response

import com.example.home_recipe.domain.user.Role

data class AdminUserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role
)
