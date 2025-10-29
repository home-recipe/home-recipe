package com.example.home_recipe.controller.auth

import com.example.home_recipe.controller.auth.dto.TokenDto
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.service.auth.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: TokenDto): ResponseEntity<ApiResponse<TokenDto>> {
        return ApiResponse.success(
            authService.refreshToken(request),
            ResponseCode.AUTH_RENEWAL_SUCCESS
        )
    }
}