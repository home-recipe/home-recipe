package com.example.home_recipe.controller.ingredient.dto.response

import com.example.home_recipe.domain.ingredient.IngredientCategory

data class IngredientResponse(
    val category: IngredientCategory,
    val name: String
)