package com.example.home_recipe.service.user

import com.example.home_recipe.controller.dto.DtoMapper.Companion.toJoinResponse
import com.example.home_recipe.controller.dto.user.dto.JoinRequest
import com.example.home_recipe.controller.dto.user.dto.JoinResponse
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun join(request: JoinRequest): JoinResponse {
        val email = request.email
        val encryptedPassword = passwordEncoder.encode(request.password)
        val name = request.name

        if (userRepository.existsByEmail(email)) {
            throw BusinessException(UserCode.SIGNUP_ERROR_005, HttpStatus.BAD_REQUEST)
        }

        return toJoinResponse(
            userRepository.save(
                User(
                    password = encryptedPassword,
                    name = name,
                    email = email,
                    role = Role.USER
                )
            )
        )
    }
}

