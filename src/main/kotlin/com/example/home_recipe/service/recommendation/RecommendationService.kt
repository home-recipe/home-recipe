package com.example.home_recipe.service.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.openai.client.OpenAIClientAsync
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val openAiClient: OpenAIClientAsync,
    val refrigeratorService: RefrigeratorService
) {


    fun chat(email: String): RecommendationsResponse {
        val params = ChatCompletionCreateParams.builder()
            .addSystemMessage(RecommendationPrompt.SYSTEM_PROMPT)
            .addUserMessage(RecommendationPrompt.userPrompt(refrigeratorService.getMyIngredientsOnlyName(email)))
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