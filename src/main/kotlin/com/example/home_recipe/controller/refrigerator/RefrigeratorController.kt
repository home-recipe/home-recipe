package com.example.home_recipe.controller.refrigerator

import com.example.home_recipe.controller.user.dto.response.EmailPrincipal
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.RefrigeratorCode
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/refrigerator")
class RefrigeratorController(
    private val refrigeratorService: RefrigeratorService
) {
    @PostMapping
    fun create(authentication: Authentication): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.createForUser(authentication.name)
        return ApiResponse.success(true, RefrigeratorCode.CREATE_SUCCESS, HttpStatus.CREATED)
    }

    @PutMapping("/ingredient/{ingredientId}")
    fun add(authentication: Authentication, @PathVariable ingredientId: Long): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.addIngredient(authentication.name, ingredientId)
        return ApiResponse.success(true, RefrigeratorCode.ADD_INGREDIENT_SUCCESS, HttpStatus.OK)
    }

    @DeleteMapping("/ingredient/{ingredientId}")
    fun use(authentication: Authentication, @PathVariable ingredientId: Long): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.useIngredient(authentication.name, ingredientId)
        return ApiResponse.success(true, RefrigeratorCode.USE_INGREDIENT_SUCCESS, HttpStatus.OK)
    }
}