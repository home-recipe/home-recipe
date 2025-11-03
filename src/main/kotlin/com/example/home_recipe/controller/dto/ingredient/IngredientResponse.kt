package com.example.home_recipe.controller.dto.ingredient

import com.example.home_recipe.domain.ingredient.IngredientCategory

data class IngredientResponse(
    val id: Long,
    val category: IngredientCategory,
    val name: String
)