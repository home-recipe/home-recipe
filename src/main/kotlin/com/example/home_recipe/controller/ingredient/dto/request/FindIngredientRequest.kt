package com.example.home_recipe.controller.ingredient.dto.request

import jakarta.validation.constraints.NotNull

data class FindIngredientRequest(
    @NotNull
    val name: String
)
