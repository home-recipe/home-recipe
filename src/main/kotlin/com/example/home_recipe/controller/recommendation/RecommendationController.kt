package com.example.home_recipe.controller.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.controller.user.dto.response.EmailPrincipal
import com.example.home_recipe.service.recommendation.RecommendationService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @PostMapping("/recommendation")
    fun getRecommendation(@AuthenticationPrincipal principal : EmailPrincipal): RecommendationsResponse {
        return recommendationService.chat(principal.email)
    }
}