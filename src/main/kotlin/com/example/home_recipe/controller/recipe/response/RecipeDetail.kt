package com.example.home_recipe.controller.recipe.response

data class RecipeDetailResponse(
    val recipeName: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val imageUrl: String? = null
)
