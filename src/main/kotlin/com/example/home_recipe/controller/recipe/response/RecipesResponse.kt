package com.example.home_recipe.controller.recipe.response

data class RecipesResponse(
    val decision: RecipeDecision,
    val reason: String,
    val recipes: List<RecipeDetail>
)