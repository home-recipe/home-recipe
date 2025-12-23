package com.example.home_recipe.controller.auth

import com.example.home_recipe.controller.auth.dto.request.RefreshTokenRequest
import com.example.home_recipe.controller.auth.dto.response.AccessTokenResponse
import com.example.home_recipe.controller.auth.dto.response.MobileLoginResponse
import com.example.home_recipe.controller.auth.dto.response.WebLoginResponse
import com.example.home_recipe.controller.user.dto.request.LoginRequest
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.service.auth.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/login")
    fun login(
        @RequestHeader("X-Client-Type") clientType: ClientType,
        @Valid @RequestBody request: LoginRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<*> {

        val response = authService.login(request)

        if (clientType == ClientType.WEB) {
            val refreshCookie = ResponseCookie.from("refreshToken", response.refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth/refresh")
                .maxAge(jwtTokenProvider.getRemainingSeconds(response.refreshToken))
                .build()

            httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            val body = WebLoginResponse(response.accessToken)
            return ApiResponse.success(body, AuthCode.AUTH_LOGIN_SUCCESS, HttpStatus.OK)
        }

        return ApiResponse.success(
            MobileLoginResponse(response.accessToken, response.refreshToken),
            AuthCode.AUTH_LOGIN_SUCCESS,
            HttpStatus.OK
        )
    }

    @PostMapping("/logout")
    fun logout(@Valid @RequestBody refreshToken: RefreshTokenRequest): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.success(
            authService.logout(refreshToken.refreshToken),
            AuthCode.AUTH_LOGOUT_SUCCESS,
            HttpStatus.OK
        )
    }

    @PostMapping("/reissue")
    fun reissueAccessToken(@Valid @RequestBody refreshToken: RefreshTokenRequest): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        return ApiResponse.success(
            authService.reissueAccessToken(refreshToken.refreshToken),
            AuthCode.AUTH_REISSUE_SUCCESS,
            HttpStatus.OK
        )
    }
}