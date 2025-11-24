package com.example.home_recipe.recipe.controller.dto.response

data class RecipeDetail(
    val recipeName: String,
    val ingredients: List<String>,
    val steps: List<String>
)
