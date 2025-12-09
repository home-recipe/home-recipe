package com.example.home_recipe.service.recipe

import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.service.user.UserService
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RecipeService(
    @Value("\${openai.api-key}") val apiKey: String,
    val userService: UserService
) {
    val client = OpenAIOkHttpClientAsync.builder()
        .apiKey(apiKey)
        .build()

    fun chat(email: String): RecipesResponse {
        val params = ChatCompletionCreateParams.builder()
            .addSystemMessage(RecipePrompt.SYSTEM_PROMPT)
            .addUserMessage(RecipePrompt.userPrompt(userService.getAllIngredientsOfUser(email)))
            .model(ChatModel.GPT_5_MINI)
            .responseFormat(RecipesResponse::class.java)
            .build()

        val response = client.chat().completions().create(params).join()

        val contents = response.choices()
            .firstOrNull()
            ?.message()
            ?.content()

        if (contents == null || contents.get().recipes.isEmpty()) {
            throw IllegalStateException("요리할 수 있는게 없어요 ㅠ_ㅠ 배달 ㄱㄱ ")
        }

        return contents.get()
    }
}