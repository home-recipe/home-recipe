package com.example.home_recipe.service.user

import com.example.home_recipe.controller.refrigerator.dto.UserJoinedEvent
import com.example.home_recipe.controller.user.dto.request.JoinRequest
import com.example.home_recipe.controller.user.dto.response.JoinResponse
import com.example.home_recipe.controller.user.dto.response.UserResponseAssembler
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun join(request: JoinRequest): JoinResponse {
        val email = request.email
        val encryptedPassword = passwordEncoder.encode(request.password)
        val name = request.name

        if (userRepository.existsByEmail(email)) {
            throw BusinessException(UserCode.SIGNUP_ERROR_005, HttpStatus.BAD_REQUEST)
        }

        val savedUser = userRepository.save(
            User(
                password = encryptedPassword,
                name = name,
                email = email,
                role = Role.USER
            )
        )

        eventPublisher.publishEvent(
            UserJoinedEvent(
                userId = savedUser.id!!,
                email = savedUser.email
            )
        )

        return UserResponseAssembler.toJoinResponse(
            userRepository.save(savedUser)
        )
    }

    fun getUser(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { BusinessException(UserCode.LOGIN_ERROR_002, HttpStatus.UNAUTHORIZED) }
    }

    fun isExistUser(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}

