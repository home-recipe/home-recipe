package com.example.home_recipe.controller.admin.response

import com.example.home_recipe.domain.user.User

object ManageResponseAssembler {

    fun toUserResponse(user: User): ManageUserResponse {
        return ManageUserResponse(
            requireNotNull(user.id),
            user.name,
            user.email,
            user.role
        )
    }

    fun toUsersResponse(users: List<User>): List<ManageUserResponse> {
        return users.map { user ->
            ManageUserResponse(
                id = requireNotNull(user.id),
                name = user.name,
                email = user.email,
                role = user.role
            )
        }
    }
}