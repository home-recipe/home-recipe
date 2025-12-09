package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.auth.dto.response.LoginResponse
import com.example.home_recipe.controller.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.user.dto.request.LoginRequest
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.service.user.UserService
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        val user = userService.getUser(request.email)
        checkPassword(request.password, user.password)

        val accessToken = jwtTokenProvider.createAccessToken(user.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email)
        tokenService.synchronizeRefreshToken(user, refreshToken)
        return LoginResponse(accessToken, refreshToken)
    }

    private fun checkPassword(rawPassword: String, encryptedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw BusinessException(UserCode.LOGIN_ERROR_003, HttpStatus.UNAUTHORIZED)
        }
    }

    @Transactional
    fun reissueAccessToken(email: String): AccessTokenResponse {
        val isExistUser = userService.isExistUser(email)
        if(!isExistUser) {
            throw BusinessException(AuthCode.AUTH_INVALID_TOKEN, HttpStatus.UNAUTHORIZED)
        }
        val accessToken = jwtTokenProvider.createAccessToken(email)
        return AccessTokenResponse(accessToken)
    }

    @Transactional
    fun logout(email: String) {
        val user = userService.getUser(email)
        val refreshToken = tokenService.getRefreshTokenByUser(user)
        tokenService.deleteRefreshToken(refreshToken)
    }
}