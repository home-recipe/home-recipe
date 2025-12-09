package com.example.home_recipe.service.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.service.recipe.RecipePrompt
import com.example.home_recipe.service.user.UserService
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    @Value("\${openai.api-key}") val apiKey : String,
    val userService: UserService
) {

    val client = OpenAIOkHttpClientAsync.builder()
        .apiKey(apiKey)
        .build()

    fun chat(email: String) : RecommendationsResponse {
        val params = ChatCompletionCreateParams.builder()
            .addSystemMessage(RecommendationPrompt.SYSTEM_PROMPT)
            .addUserMessage(RecommendationPrompt.userPrompt(userService.getAllIngredientsOfUser(email)))
            .model(ChatModel.GPT_5_MINI)
            .responseFormat(RecommendationsResponse::class.java)
            .build()

        val response = client.chat().completions().create(params).join()

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