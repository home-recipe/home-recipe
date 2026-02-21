package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.auth.dto.request.LoginRequest
import com.example.home_recipe.controller.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.auth.dto.response.LoginResponse
import com.example.home_recipe.controller.auth.dto.response.TokenResponse
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.service.user.UserService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.Base64

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val authCodeCacheService: AuthCodeCacheService,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        val user = userService.getUser(request.email)
        checkPassword(request.password, user.password)

        val accessToken = jwtTokenProvider.createAccessToken(user.email, user.role)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email, user.role)
        tokenService.synchronizeRefreshToken(user, refreshToken)
        return LoginResponse(accessToken, refreshToken, user.role)
    }

    private fun checkPassword(rawPassword: String, encryptedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw BusinessException(UserCode.LOGIN_ERROR_003, HttpStatus.UNAUTHORIZED)
        }
    }

    @Transactional
    fun reissueAccessToken(email: String, providedRefreshToken: String): AccessTokenResponse {
        val user = userService.getUser(email)
        jwtTokenProvider.validateToken(providedRefreshToken)
        val savedRefreshToken = tokenService.getRefreshTokenByUser(user)

        if (providedRefreshToken != savedRefreshToken.refreshToken) {
            throw BusinessException(AuthCode.AUTH_INVALID_TOKEN, HttpStatus.UNAUTHORIZED)
        }
        val accessToken = jwtTokenProvider.createAccessToken(email, user.role)
        return AccessTokenResponse(accessToken)
    }

    @Transactional
    fun logout(email: String) {
        val user = userService.getUser(email)
        val refreshToken = tokenService.getRefreshTokenByUser(user)
        tokenService.deleteRefreshToken(refreshToken)
    }

    @Transactional
    fun exchangeToken(code: String, codeVerifier: String): TokenResponse {
        val entry = authCodeCacheService.find(code)
            ?: throw BusinessException(AuthCode.AUTH_INVALID_AUTH_CODE, HttpStatus.UNAUTHORIZED)

        val digest = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
        val computedChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest)

        if (computedChallenge != entry.challenge) {
            log.warn("PKCE 검증 실패 - code: {}, 기대: {}, 실제: {}", code, entry.challenge, computedChallenge)
            authCodeCacheService.evict(code)
            throw BusinessException(AuthCode.AUTH_PKCE_VERIFICATION_FAILED, HttpStatus.UNAUTHORIZED)
        }

        val user = userService.getUser(entry.email)
        val accessToken = jwtTokenProvider.createAccessToken(user.email, user.role)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email, user.role)
        tokenService.synchronizeRefreshToken(user, refreshToken)

        authCodeCacheService.evict(code)
        log.info("PKCE 토큰 발급 완료 - email: {}", entry.email)

        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken, role = user.role)
    }
}