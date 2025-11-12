package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.dto.auth.dto.TokenDto
import com.example.home_recipe.controller.dto.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.dto.user.dto.request.LoginRequest
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.UserRepository
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
    fun login(request: LoginRequest): TokenDto {
        val user = userService.getUser(request.email)
        checkPassword(request.password, user.password)

        val accessToken = jwtTokenProvider.createAccessToken(user.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email)
        tokenService.synchronizeRefreshToken(user, refreshToken)
        return TokenDto(accessToken, refreshToken)
    }

    fun checkPassword(rawPassword: String, encryptedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw BusinessException(UserCode.LOGIN_ERROR_003, HttpStatus.UNAUTHORIZED)
        }
    }

    fun reissueAccessToken(refreshToken: String): AccessTokenResponse {
        jwtTokenProvider.validateToken(refreshToken)
        val validRefreshToken = tokenService.getValidRefreshToken(refreshToken)
        val user = validRefreshToken.user
        val newAccessToken = jwtTokenProvider.createAccessToken(user.email)
        return AccessTokenResponse(newAccessToken)
    }

    @Transactional
    fun logout(email: String) {
        val user = userService.getUser(email)
        val refreshToken = tokenService.getRefreshTokenByUserEmail(email)
        tokenService.deleteRefreshToken(refreshToken)
    }
}