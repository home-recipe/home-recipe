package com.example.home_recipe.recipe.controller

import com.example.home_recipe.recipe.controller.dto.response.RecipesResponse
import com.example.home_recipe.recipe.service.RecipeService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipeController(
    private val recipeService: RecipeService
) {

    @PostMapping("/recipes")
    fun getRecipes(@RequestParam ingredients : List<String>) :RecipesResponse {
        val prompt = ingredients.joinToString(", ")
        return recipeService.chat(prompt)
    }
}