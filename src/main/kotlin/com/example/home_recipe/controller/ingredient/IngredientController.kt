package com.example.home_recipe.controller.ingredient

import com.example.home_recipe.controller.ingredient.dto.request.CreateIngredientBatchRequest
import com.example.home_recipe.controller.ingredient.dto.request.CreateIngredientRequest
import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.request.UpdateIngredientRequest
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.IngredientCode
import com.example.home_recipe.service.ingredient.IngredientService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/ingredients")
class IngredientController(
    private val ingredientService: IngredientService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreateIngredientRequest): ResponseEntity<ApiResponse<IngredientResponse>> {
        val result = ingredientService.create(request)
        return ApiResponse.success(result, IngredientCode.CREATE_SUCCESS, HttpStatus.CREATED)
    }

    @PostMapping("/batch") fun createAll(
        @Valid @RequestBody request: CreateIngredientBatchRequest): ResponseEntity<ApiResponse<List<IngredientResponse>>> {
        val result = ingredientService.createAll(request.items)
        return ApiResponse.success(result, IngredientCode.CREATE_SUCCESS, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: UpdateIngredientRequest): ResponseEntity<ApiResponse<IngredientResponse>> {
        val result = ingredientService.update(id, request)
        return ApiResponse.success(result, IngredientCode.UPDATE_SUCCESS, HttpStatus.OK)
    }
}
