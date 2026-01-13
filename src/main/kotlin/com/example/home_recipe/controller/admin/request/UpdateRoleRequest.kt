package com.example.home_recipe.controller.admin.request

import com.example.home_recipe.domain.user.Role
import jakarta.validation.constraints.NotNull

data class UpdateRoleRequest(
    @NotNull
    val id : Long,
    @NotNull
    val role: Role
)
