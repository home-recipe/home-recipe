package com.example.home_recipe.controller.refrigerator

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.RefrigeratorCode
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/refrigerator")
class RefrigeratorController(
    private val refrigeratorService: RefrigeratorService
) {
    @PostMapping
    fun create(@AuthenticationPrincipal user: UserDetails): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.createForUser(user.username)
        return ApiResponse.success(true, RefrigeratorCode.CREATE_SUCCESS, HttpStatus.CREATED)
    }

    @PutMapping("/ingredient/{ingredientId}")
    fun add(@AuthenticationPrincipal user: UserDetails, @PathVariable ingredientId: Long): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.addIngredient(user.username, ingredientId)
        return ApiResponse.success(true, RefrigeratorCode.ADD_INGREDIENT_SUCCESS, HttpStatus.OK)
    }

    @DeleteMapping("/ingredient/{ingredientId}")
    fun use(@AuthenticationPrincipal user: UserDetails, @PathVariable ingredientId: Long): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.useIngredient(user.username, ingredientId)
        return ApiResponse.success(true, RefrigeratorCode.USE_INGREDIENT_SUCCESS, HttpStatus.OK)
    }
}