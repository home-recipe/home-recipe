package com.example.home_recipe.service.user

import com.example.home_recipe.controller.user.dto.JoinRequest
import com.example.home_recipe.controller.user.dto.UserResponse
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
) {
    fun join(request : JoinRequest) : UserResponse {
        return toDto(
            userRepository.save(
                User(
                    request.name,
                    request.loginId,
                    request.email,
                    request.phoneNumber,
                    request.phoneNumber

                )
            )
        )
    }

    fun toDto(user : User) : UserResponse {
        return UserResponse(
            name = user.name,
            loginId = user.loginId,
            email = user.email,
            phoneNumber = user.phoneNumber,
            role = user.role.name
        )
    }
}

