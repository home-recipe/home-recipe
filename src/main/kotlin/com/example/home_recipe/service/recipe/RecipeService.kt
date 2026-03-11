package com.example.home_recipe.service.recipe

import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.RecipeCode
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.openai.client.OpenAIClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val openAiClient: OpenAIClientAsync,
    private val refrigeratorService: RefrigeratorService,
    private val gptCacheService: GptCacheService
) {

    fun chat(email: String): RecipesResponse {
        val ingredients = refrigeratorService.getMyIngredientsOnlyName(email)

        return gptCacheService.getOrCompute(
            feature = "RECIPE",
            model = ChatModel.GPT_5_MINI.toString(),
            promptVersion = RecipePrompt.VERSION,
            ingredientsRaw = ingredients,
            responseClass = RecipesResponse::class.java
        ) {
            val params = ChatCompletionCreateParams.builder()
                .addSystemMessage(RecipePrompt.SYSTEM_PROMPT)
                .addUserMessage(RecipePrompt.userPrompt(ingredients))
                .model(ChatModel.GPT_5_MINI)
                .responseFormat(RecipesResponse::class.java)
                .build()

            val response = openAiClient.chat().completions().create(params).join()

            val contents = response.choices()
                .firstOrNull()
                ?.message()
                ?.content()

            if (contents == null) {
                throw BusinessException(RecipeCode.RECIPE_ERROR_001, HttpStatus.INTERNAL_SERVER_ERROR)
            }

            contents.get()
        }
    }
}