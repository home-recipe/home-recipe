package com.example.home_recipe.controller.dto

import com.example.home_recipe.controller.dto.user.dto.JoinResponse
import com.example.home_recipe.domain.user.User

class DtoMapper {

    companion object {
        fun toJoinResponse(user: User): JoinResponse {
            return JoinResponse(
                name = user.name,
                email = user.email,
                role = user.role.name
            )
        }
    }
}