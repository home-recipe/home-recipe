package com.example.home_recipe.recommendation.controller.dto.response

data class RecommendationDetail(
    val recipeName: String,
    val ingredients: List<String>
)
