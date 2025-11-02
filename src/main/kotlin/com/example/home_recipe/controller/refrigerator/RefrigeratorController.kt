package com.example.home_recipe.controller.refrigerator

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.RefrigeratorCode
import com.example.home_recipe.global.sercurity.annotation.UserEmail
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/refrigerator")
class RefrigeratorController(
    private val refrigeratorService: RefrigeratorService
) {
    @PostMapping
    fun create(@UserEmail email: String): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.createForUser(email)
        return ApiResponse.success(true, RefrigeratorCode.CREATE_SUCCESS, HttpStatus.CREATED)
    }

    @PutMapping("/ingredient/{ingredientId}")
    fun add(@UserEmail email: String, @PathVariable ingredientId: Long): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.addIngredient(email, ingredientId)
        return ApiResponse.success(true, RefrigeratorCode.ADD_INGREDIENT_SUCCESS, HttpStatus.OK)
    }

    @DeleteMapping("/ingredient/{ingredientId}")
    fun use(@UserEmail email: String, @PathVariable ingredientId: Long): ResponseEntity<ApiResponse<Boolean>> {
        refrigeratorService.useIngredient(email, ingredientId)
        return ApiResponse.success(true, RefrigeratorCode.USE_INGREDIENT_SUCCESS, HttpStatus.OK)
    }
}