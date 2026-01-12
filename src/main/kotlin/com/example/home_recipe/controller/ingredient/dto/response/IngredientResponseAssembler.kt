package com.example.home_recipe.controller.ingredient.dto.response

import com.example.home_recipe.domain.ingredient.Ingredient

object IngredientResponseAssembler {
    fun toIngredientResponse(ingredient: Ingredient): IngredientResponse {
        return IngredientResponse(
            ingredient.id,
            ingredient.category,
            ingredient.name,
            Source.DATABASE
        )
    }

    fun toIngredientResponseList(ingredients: List<Ingredient>): List<IngredientResponse> {
        return ingredients.map { ingredient -> toIngredientResponse(ingredient) }
    }
}