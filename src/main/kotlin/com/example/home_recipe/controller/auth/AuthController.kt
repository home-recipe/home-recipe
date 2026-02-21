package com.example.home_recipe.controller.auth

import com.example.home_recipe.controller.auth.dto.request.LoginRequest
import com.example.home_recipe.controller.auth.dto.request.TokenRequest
import com.example.home_recipe.controller.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.auth.dto.response.LoginCodeResponse
import com.example.home_recipe.controller.auth.dto.response.TokenResponse
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.service.auth.AuthHelper
import com.example.home_recipe.service.auth.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val authHelper: AuthHelper
) {

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<ApiResponse<LoginCodeResponse>> {
        val loginCodeResponse = authService.login(request)
        return ApiResponse.success(loginCodeResponse, AuthCode.AUTH_LOGIN_SUCCESS, HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(authentication: Authentication): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.success(authService.logout(authentication.name), AuthCode.AUTH_LOGOUT_SUCCESS, HttpStatus.OK)
    }

    @PostMapping("/token")
    fun exchangeToken(
        @Valid @RequestBody request: TokenRequest,
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        val tokenResponse = authService.exchangeToken(request.code, request.codeVerifier)
        return ApiResponse.success(tokenResponse, AuthCode.AUTH_TOKEN_ISSUED, HttpStatus.OK)
    }

    @PostMapping("/reissue")
    fun reissueAccessToken(
        authentication: Authentication,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val refreshToken = authHelper.extractRefreshToken(request)

        if (refreshToken.isNullOrBlank()) {
            return ApiResponse.success(null, AuthCode.NOT_EXIST_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED)
        }

        return ApiResponse.success(
            authService.reissueAccessToken(authentication.name, refreshToken),
            AuthCode.AUTH_REISSUE_SUCCESS,
            HttpStatus.OK
        )
    }
}