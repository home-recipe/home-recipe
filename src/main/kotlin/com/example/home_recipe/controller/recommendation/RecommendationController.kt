package com.example.home_recipe.controller.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.controller.user.dto.response.EmailPrincipal
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.RecommendationCode
import com.example.home_recipe.service.recommendation.RecommendationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @PostMapping("/recommendation")
    fun getRecommendation(authentication: Authentication): ResponseEntity<ApiResponse<RecommendationsResponse>> {
        return ApiResponse.success(recommendationService.chat(authentication.name),
            RecommendationCode.RECOMMENDATION_SUCCESS, HttpStatus.CREATED
        )
    }
}