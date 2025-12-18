package com.example.home_recipe.controller.user

import com.example.home_recipe.controller.user.dto.request.EmailRequest
import com.example.home_recipe.controller.user.dto.request.JoinRequest
import com.example.home_recipe.controller.user.dto.response.JoinResponse
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    fun join(@Valid @RequestBody request: JoinRequest): ResponseEntity<ApiResponse<JoinResponse>> {
        return ApiResponse.success(userService.join(request), UserCode.SIGNUP_SUCCESS, HttpStatus.CREATED)
    }

    @PostMapping("/email")
    fun validateEmail(@Valid @RequestBody request: EmailRequest) : ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.success(userService.validateEmail(request), UserCode.EMAIL_VALIDATION_SUCCESS, HttpStatus.OK)
    }
}