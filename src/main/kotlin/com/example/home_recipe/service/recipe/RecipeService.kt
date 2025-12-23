package com.example.home_recipe.service.recipe

import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.RecipeCode
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class RecipeService(
    @Value("\${openai.api-key}") val apiKey: String,
    val refrigeratorService: RefrigeratorService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val client = OpenAIOkHttpClientAsync.builder()
        .apiKey(apiKey)
        .build()

    fun chat(email: String): RecipesResponse {
        val params = ChatCompletionCreateParams.builder()
            .addSystemMessage(RecipePrompt.SYSTEM_PROMPT)
            .addUserMessage(RecipePrompt.userPrompt(refrigeratorService.getMyIngredientNames(email)))
            .model(ChatModel.GPT_5_MINI)
            .responseFormat(RecipesResponse::class.java)
            .build()

        val response = client.chat().completions().create(params).join()

        val contents = response.choices()
            .firstOrNull()
            ?.message()
            ?.content()

        if (contents == null) {
            log.error("GPT returned null content. response={}", response)
            throw BusinessException(RecipeCode.RECIPE_ERROR_001, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        log.info("chat GPT raw response = {}", contents)
        return contents.get()
    }
}