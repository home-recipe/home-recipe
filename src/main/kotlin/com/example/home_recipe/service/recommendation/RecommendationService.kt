package com.example.home_recipe.service.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.domain.recipe.RecipeCache
import com.example.home_recipe.global.util.IngredientHashUtil
import com.example.home_recipe.repository.RecipeCacheRepository
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.fasterxml.jackson.databind.ObjectMapper
import com.openai.client.OpenAIClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val openAiClient: OpenAIClientAsync,
    val refrigeratorService: RefrigeratorService,
    private val recipeCacheRepository: RecipeCacheRepository,
    private val objectMapper: ObjectMapper
) {

    fun chat(email: String): RecommendationsResponse {
        val ingredients = refrigeratorService.getMyIngredientsOnlyName(email)
        val cacheKey = IngredientHashUtil.generateCacheKey("recommendation", ingredients)

        val cached = recipeCacheRepository.findById(cacheKey)
        if (cached.isPresent) {
            return objectMapper.readValue(cached.get().recipeContent, RecommendationsResponse::class.java)
        }

        val result = callOpenAi(ingredients)

        val cacheEntry = RecipeCache(
            id = cacheKey,
            recipeContent = objectMapper.writeValueAsString(result)
        )
        recipeCacheRepository.save(cacheEntry)

        return result
    }

    private fun callOpenAi(ingredients: List<String>): RecommendationsResponse {
        val params = ChatCompletionCreateParams.builder()
            .addSystemMessage(RecommendationPrompt.SYSTEM_PROMPT)
            .addUserMessage(RecommendationPrompt.userPrompt(ingredients))
            .model(ChatModel.GPT_5_MINI)
            .responseFormat(RecommendationsResponse::class.java)
            .build()

        val response = openAiClient.chat().completions().create(params).join()

        val contents = response.choices()
            .firstOrNull()
            ?.message()
            ?.content()
            ?.get()

        if (contents == null || contents.recommendations.isEmpty()) {
            throw IllegalStateException("추천 가능한 레시피가 없어요 ㅠ_ㅠ")
        }

        return contents
    }
}
