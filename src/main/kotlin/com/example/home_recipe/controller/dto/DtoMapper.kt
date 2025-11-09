package com.example.home_recipe.controller.dto

import com.example.home_recipe.controller.dto.ingredient.IngredientResponse
import com.example.home_recipe.controller.dto.user.dto.JoinResponse
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.user.User

class DtoMapper {

    companion object {
        fun toJoinResponse(user: User): JoinResponse {
            return JoinResponse(
                name = user.name,
                email = user.email,
                role = user.role.name
            )
        }

        fun toIngredientResponse(ingredient: Ingredient): IngredientResponse {
            return IngredientResponse(
                category = ingredient.category,
                name = ingredient.name
            )
        }

        fun toIngredientResponses(ingredients: Iterable<Ingredient>) =
            ingredients.map { toIngredientResponse(it) }
    }
}