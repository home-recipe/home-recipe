package com.example.home_recipe.service.ingredient

import com.example.home_recipe.domain.ingredient.IngredientCategory

data class StandardInfo(
    val isIngredient: Boolean,
    val standardName: String,
    val category: IngredientCategory
)
