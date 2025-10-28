package com.example.home_recipe.service.user

import com.example.home_recipe.controller.user.dto.JoinRequest
import com.example.home_recipe.controller.user.dto.UserResponse
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun join(request : JoinRequest) : UserResponse {
        val loginId = request.name
        val email = request.email
        val phoneNumber = request.phoneNumber
        val encryptedPassword = passwordEncoder.encode(request.password)
        val name = request.name

        if(userRepository.existsByLoginId(loginId)) {
            throw BusinessException(ResponseCode.SIGNUP_ERROR_007)
        }
        if(userRepository.existsByEmail(email)) {
            throw BusinessException(ResponseCode.SIGNUP_ERROR_005)
        }
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw BusinessException(ResponseCode.SIGNUP_ERROR_008)
        }

        return toDto(userRepository.save(User(
            loginId = loginId,
            password = encryptedPassword,
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            role = Role.USER
        )))
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

