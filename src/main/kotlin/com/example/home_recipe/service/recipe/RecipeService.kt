package com.example.home_recipe.service.recipe

import com.example.home_recipe.controller.recipe.response.RecipeDetailResponse
import com.example.home_recipe.controller.recipe.response.RecipeDecision
import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.domain.recipe.RecipeDetail
import com.example.home_recipe.domain.recipe.RecipeSet
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.RecipeCode
import com.example.home_recipe.global.util.IngredientHashUtil
import com.example.home_recipe.repository.RecipeSetRepository
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.example.home_recipe.service.storage.ImageStorageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.openai.client.OpenAIClientAsync
import com.openai.models.ChatModel
import com.openai.models.chat.completions.ChatCompletionCreateParams
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RecipeService(
    private val openAiClient: OpenAIClientAsync,
    val refrigeratorService: RefrigeratorService,
    private val recipeSetRepository: RecipeSetRepository,
    private val objectMapper: ObjectMapper,
    private val geminiImageService: GeminiImageService,
    private val imageStorageService: ImageStorageService
) {

    @Transactional
    fun chat(email: String): RecipesResponse {
        val ingredients = refrigeratorService.getMyIngredientsOnlyName(email)
        val cacheKey = IngredientHashUtil.generateCacheKey(ingredients)

        val cached = recipeSetRepository.findByIdWithDetails(cacheKey)
        if (cached != null) {
            return toResponse(cached)
        }

        val aiResponse = callOpenAi(ingredients)

        val recipeSet = RecipeSet(
            id = cacheKey,
            ingredientsList = ingredients.sorted().joinToString(","),
            decision = RecipeSet.Decision.valueOf(aiResponse.decision.name),
            reason = aiResponse.reason
        )

        for (recipe in aiResponse.recipes) {
            val imageUrl = generateAndUploadImage(recipe.recipeName, cacheKey)

            val detail = RecipeDetail(
                recipeSet = recipeSet,
                recipeName = recipe.recipeName,
                ingredients = objectMapper.writeValueAsString(recipe.ingredients),
                steps = objectMapper.writeValueAsString(recipe.steps),
                imageUrl = imageUrl
            )
            recipeSet.addDetail(detail)
        }

        recipeSetRepository.save(recipeSet)

        return toResponse(recipeSet)
    }

    private fun generateAndUploadImage(recipeName: String, setId: String): String? {
        val imageBytes = geminiImageService.generateImage(recipeName) ?: return null
        val fileName = "${setId}_${UUID.randomUUID()}.png"
        return imageStorageService.upload(imageBytes, fileName, "image/png")
    }

    private fun callOpenAi(ingredients: List<String>): RecipesResponse {
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

        return contents.get()
    }

    /** RecipeSet 엔티티를 Response DTO로 변환한다 */
    private fun toResponse(recipeSet: RecipeSet): RecipesResponse {
        return RecipesResponse(
            decision = RecipeDecision.valueOf(recipeSet.decision.name),
            reason = recipeSet.reason ?: "",
            recipes = recipeSet.recipeDetails.map { detail ->
                RecipeDetailResponse(
                    recipeName = detail.recipeName,
                    ingredients = objectMapper.readValue(
                        detail.ingredients,
                        objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
                    ),
                    steps = objectMapper.readValue(
                        detail.steps,
                        objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
                    ),
                    imageUrl = detail.imageUrl
                )
            }
        )
    }
}
