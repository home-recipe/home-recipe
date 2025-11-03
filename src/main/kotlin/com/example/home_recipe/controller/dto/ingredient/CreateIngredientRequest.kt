package com.example.home_recipe.controller.dto.ingredient

import com.example.home_recipe.domain.ingredient.IngredientCategory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateIngredientRequest(
    @field:NotNull(message = "INGREDIENT_ERROR_013")
    val category: IngredientCategory?,

    @field:NotBlank(message = "INGREDIENT_ERROR_005")
    @field:Size(max = 100, message = "INGREDIENT_ERROR_012")
    val name: String
)