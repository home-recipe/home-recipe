package com.example.home_recipe.controller.recipe.response

/** 레시피 응답 DTO (프론트엔드 전달용) */
data class RecipesResponse(
    val decision: RecipeDecision,
    val reason: String,
    val recipes: List<RecipeDetailResponse>
)
