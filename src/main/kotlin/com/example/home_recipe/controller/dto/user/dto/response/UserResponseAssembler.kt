package com.example.home_recipe.controller.dto.user.dto.response

import com.example.home_recipe.domain.user.User

object UserResponseAssembler {

    fun toJoinResponse(user: User): JoinResponse {
        return JoinResponse(
            user.name,
            user.email,
            user.role.name
        )
    }
}