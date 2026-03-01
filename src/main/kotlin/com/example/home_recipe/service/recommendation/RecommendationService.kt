package com.example.home_recipe.service.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.domain.recipe.RecommendationCache
import com.example.home_recipe.global.util.IngredientHashUtil
import com.example.home_recipe.repository.RecommendationCacheRepository
import com.example.home_recipe.service.recipe.GeminiTextService
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val geminiTextService: GeminiTextService,
    val refrigeratorService: RefrigeratorService,
    private val recommendationCacheRepository: RecommendationCacheRepository,
    private val objectMapper: ObjectMapper
) {

    fun chat(email: String): RecommendationsResponse {
        val ingredients = refrigeratorService.getMyIngredientsOnlyName(email)
        val cacheKey = IngredientHashUtil.generateCacheKey("recommendation", ingredients)

        val cached = recommendationCacheRepository.findById(cacheKey)
        if (cached.isPresent) {
            return objectMapper.readValue(cached.get().recommendationContent, RecommendationsResponse::class.java)
        }

        val result = callGemini(ingredients)

        val cacheEntry = RecommendationCache(
            id = cacheKey,
            recommendationContent = objectMapper.writeValueAsString(result)
        )
        recommendationCacheRepository.save(cacheEntry)

        return result
    }

    private fun callGemini(ingredients: List<String>): RecommendationsResponse {
        val result = geminiTextService.generate(
            systemPrompt = RecommendationPrompt.SYSTEM_PROMPT,
            userPrompt = RecommendationPrompt.userPrompt(ingredients),
            responseType = RecommendationsResponse::class.java
        )

        if (result.recommendations.isEmpty()) {
            throw IllegalStateException("추천 가능한 레시피가 없어요 ㅠ_ㅠ")
        }

        return result
    }
}
