package com.example.home_recipe.controller.ingredient.dto.response

import com.example.home_recipe.domain.ingredient.IngredientCategory

data class IngredientResponse(
    val id: Long? = null,
    val category: IngredientCategory? = null,
    val name: String,
    val source: Source
)