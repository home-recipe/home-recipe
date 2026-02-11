package com.example.home_recipe.controller.auth

import com.example.home_recipe.controller.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.auth.dto.response.LoginResponse
import com.example.home_recipe.controller.auth.dto.request.LoginRequest
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
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
    private val jwtTokenProvider: JwtTokenProvider
) {
    companion object {
        const val CLIENT_TYPE = "X-Client-Type"
        const val WEB = "WEB"
        const val MOBILE = "MOBILE"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
        const val REFRESH_TOKEN = "refreshToken"
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        return ApiResponse.success(authService.login(request), AuthCode.AUTH_LOGIN_SUCCESS, HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(authentication: Authentication): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.success(authService.logout(authentication.name), AuthCode.AUTH_LOGOUT_SUCCESS, HttpStatus.OK)
    }

    @PostMapping("/reissue")
    fun reissueAccessToken(
        authentication: Authentication,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {

        val clientType = request.getHeader(CLIENT_TYPE)?: WEB

        val refreshToken = when(clientType.uppercase()) {
            MOBILE -> {
                request.getHeader(AUTHORIZATION)
                    ?.removePrefix(BEARER)
                    ?.trim()
            }
            else -> {
                request.cookies
                    ?.find {it.name == REFRESH_TOKEN}
                    ?.value
            }
        }
        if(refreshToken.isNullOrBlank()) {
            return ApiResponse.success(null, AuthCode.NOT_EXIST_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED)
        }
        return ApiResponse.success(
            authService.reissueAccessToken(authentication.name),
            AuthCode.AUTH_REISSUE_SUCCESS,
            HttpStatus.OK
        )
    }
}