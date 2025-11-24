package com.example.home_recipe.recommendation.controller

import com.example.home_recipe.recommendation.controller.dto.response.RecommendationsResponse
import com.example.home_recipe.recommendation.service.RecommendationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @PostMapping("/recommendation")
    fun getRecommendation(@RequestParam ingredients: List<String>) :RecommendationsResponse {
        val prompt = ingredients.joinToString(", ")
        return recommendationService.chat(prompt)
    }
}