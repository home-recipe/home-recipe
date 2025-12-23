package com.example.home_recipe.controller.recipe

import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.RecipeCode
import com.example.home_recipe.service.recipe.RecipeService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RecipeController(
    private val recipeService: RecipeService
) {

    @PostMapping("/recipes")
    fun getRecipes(authentication: Authentication): ResponseEntity<ApiResponse<RecipesResponse>> {
        return ApiResponse.success(
            recipeService.chat(authentication.name),
            RecipeCode.RECIPE_SUCCESS,
            HttpStatus.CREATED
        )
    }
}