package com.example.home_recipe.controller.user

import com.example.home_recipe.controller.auth.dto.TokenDto
import com.example.home_recipe.controller.user.dto.JoinRequest
import com.example.home_recipe.controller.user.dto.LoginRequest
import com.example.home_recipe.controller.user.dto.JoinResponse
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.service.auth.AuthService
import com.example.home_recipe.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val authService: AuthService
) {

    @PostMapping
    fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<JoinResponse>> {
        return ApiResponse.success(userService.join(request), ResponseCode.SIGNUP_SUCCESS, HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<TokenDto>> {
        return ApiResponse.success(authService.login(request), ResponseCode.LOGIN_SUCCESS, HttpStatus.OK)
    }
}