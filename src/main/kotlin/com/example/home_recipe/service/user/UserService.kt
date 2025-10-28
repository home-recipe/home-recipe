package com.example.home_recipe.service.user

import com.example.home_recipe.controller.user.dto.JoinRequest
import com.example.home_recipe.controller.user.dto.UserResponse
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun join(request : JoinRequest) : UserResponse {
        return toDto(
            userRepository.save(
                User(
                    request.loginId,
                    passwordEncoder.encode(request.password),
                    request.name,
                    request.email,
                    request.phoneNumber
                )
            )
        )
    }

    fun toDto(user : User) : UserResponse {
        return UserResponse(
            loginId = user.loginId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            role = user.role.name
        )
    }
}

