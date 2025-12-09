package com.example.home_recipe.controller.recipe

import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.service.recipe.RecipeService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipeController(
    private val recipeService: RecipeService
) {

    @PostMapping("/recipes")
    fun getRecipes(@AuthenticationPrincipal email: String): RecipesResponse {
        return recipeService.chat(email)
    }
}