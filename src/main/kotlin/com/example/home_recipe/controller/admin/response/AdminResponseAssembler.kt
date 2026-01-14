package com.example.home_recipe.controller.admin.response

import com.example.home_recipe.domain.user.User

object AdminResponseAssembler {

    fun toUserResponse(user: User): AdminUserResponse {
        return AdminUserResponse(
            requireNotNull(user.id),
            user.name,
            user.email,
            user.role
        )
    }

    fun toUsersResponse(users: List<User>): List<AdminUserResponse> {
        return users.map { user ->
            AdminUserResponse(
                id = requireNotNull(user.id),
                name = user.name,
                email = user.email,
                role = user.role
            )
        }
    }
}