package com.example.home_recipe.controller.auth

import com.example.home_recipe.controller.auth.dto.response.LoginResponse
import com.example.home_recipe.controller.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.user.dto.request.LoginRequest
import com.example.home_recipe.controller.user.dto.response.EmailPrincipal
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.service.auth.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        return ApiResponse.success(authService.login(request), AuthCode.AUTH_LOGIN_SUCCESS, HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal principal : EmailPrincipal): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.success(authService.logout(principal.email), AuthCode.AUTH_LOGOUT_SUCCESS, HttpStatus.OK)
    }

    @PostMapping("/reissue")
    fun reissueAccessToken(@AuthenticationPrincipal principal : EmailPrincipal): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        return ApiResponse.success(
            authService.reissueAccessToken(principal.email),
            AuthCode.AUTH_REISSUE_SUCCESS,
            HttpStatus.OK
        )
    }
}