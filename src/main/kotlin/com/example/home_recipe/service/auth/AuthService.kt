package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.auth.dto.TokenDto
import com.example.home_recipe.controller.user.dto.LoginRequest
import com.example.home_recipe.domain.auth.RefreshToken
import com.example.home_recipe.global.sercurity.JwtProvider
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.UserRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun login(request: LoginRequest): TokenDto {
        val user = userRepository.findByLoginId(request.loginId)
            .orElseThrow { BusinessException(ResponseCode.LOGIN_ERROR_002, HttpStatus.UNAUTHORIZED) }

        if (!checkPassword(request.password, user.password)) {
            throw BusinessException(ResponseCode.LOGIN_ERROR_003, HttpStatus.UNAUTHORIZED)
        }

        val accessToken = jwtProvider.generateAccessToken(user.email)
        val refreshToken = generateToken(user.email).token
        return TokenDto(accessToken, refreshToken)
    }

    fun checkPassword(rawPassword: String, encryptedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encryptedPassword)
    }

    fun generateToken(email: String): RefreshToken {
        val refreshToken = jwtProvider.generateRefreshToken(email)
        val expiresAt = LocalDateTime.now()
            .plusSeconds(jwtProvider.getRefreshExpiration() / 1000)

        val refreshEntity = RefreshToken(
            email = email,
            token = refreshToken,
            expiresAt = expiresAt
        )
        return refreshTokenRepository.save(refreshEntity)
    }

    fun refreshToken(request: TokenDto): TokenDto {
        val refreshToken = request.refreshToken

        val responseCode = jwtProvider.validateRefreshToken(refreshToken)
        if (responseCode != ResponseCode.AUTH_SUCCESS)
            throw BusinessException(responseCode, HttpStatus.UNAUTHORIZED)

        val email = jwtProvider.getEmailFromToken(refreshToken)

        val savedToken = refreshTokenRepository.findById(email)
            .orElseThrow { BusinessException(ResponseCode.AUTH_ERROR_006, HttpStatus.UNAUTHORIZED) }

        if (savedToken.token != refreshToken)
            throw BusinessException(ResponseCode.AUTH_ERROR_006, HttpStatus.UNAUTHORIZED)

        val newAccessToken = jwtProvider.generateAccessToken(email)
        val newRefreshToken = jwtProvider.generateRefreshToken(email)

        val expiresAt = LocalDateTime.now().plusSeconds(jwtProvider.getRefreshExpiration() / 1000)
        val newEntity = RefreshToken(email, newRefreshToken, expiresAt)
        refreshTokenRepository.save(newEntity)

        return TokenDto(newAccessToken, newRefreshToken)
    }
}