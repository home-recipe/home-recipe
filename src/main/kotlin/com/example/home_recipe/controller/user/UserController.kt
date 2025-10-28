package com.example.home_recipe.controller.user

import com.example.home_recipe.controller.user.dto.JoinRequest
import com.example.home_recipe.controller.user.dto.UserResponse
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun join(@RequestBody request: JoinRequest): ResponseEntity<ApiResponse<UserResponse>> {
        return ApiResponse.success(userService.join(request), ResponseCode.SIGNUP_SUCCESS, HttpStatus.CREATED)
    }
}