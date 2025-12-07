package com.example.home_recipe.controller.ingredient.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.Size

data class CreateIngredientBatchRequest(
    @field:Size(min = 1, message = "최소 1개 이상의 재료가 필요합니다.")
    val items: List<@Valid CreateIngredientRequest>
)