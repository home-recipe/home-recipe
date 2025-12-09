package com.example.home_recipe.controller.recipe.response

data class RecipeDetail(
    val recipeName: String,
    val ingredients: List<String>,
    val steps: List<String>
)