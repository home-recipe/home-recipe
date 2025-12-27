package com.example.home_recipe.controller.refrigerator.dto.response

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse

data class RefrigeratorResponse(
    val myRefrigerator : List<IngredientResponse>
)