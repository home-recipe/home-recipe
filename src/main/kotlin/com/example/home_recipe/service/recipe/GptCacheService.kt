package com.example.home_recipe.service.recipe

import com.example.home_recipe.domain.cache.GptCache
import com.example.home_recipe.repository.GptCacheRepository
import com.example.home_recipe.service.ingredient.IngredientKey
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GptCacheService(
    private val gptCacheRepository: GptCacheRepository,
    private val objectMapper: ObjectMapper
) {

    fun <T : Any> getOrCompute(
        feature: String,
        model: String,
        promptVersion: Int,
        ingredientsRaw: List<String>,
        responseClass: Class<T>,
        compute: () -> T
    ): T {
        val normalizedIngredients = IngredientKey.normalize(ingredientsRaw)
        val ingredientsHash = IngredientKey.ingredientsHash(normalizedIngredients)

        val cached = gptCacheRepository.findByFeatureAndModelAndPromptVersionAndIngredientsHash(
            feature = feature,
            model = model,
            promptVersion = promptVersion,
            ingredientsHash = ingredientsHash
        )

        if (cached != null) {
            cached.lastHitAt = LocalDateTime.now()
            cached.hitCount += 1
            gptCacheRepository.save(cached)

            return objectMapper.readValue(cached.responseJson, responseClass)
        }

        val freshResponse = compute()

        val normalizedIngredientsJson = objectMapper.writeValueAsString(normalizedIngredients)
        val responseJson = objectMapper.writeValueAsString(freshResponse)

        try {
            gptCacheRepository.save(
                GptCache(
                    feature = feature,
                    model = model,
                    promptVersion = promptVersion,
                    ingredientsHash = ingredientsHash,
                    normalizedIngredientsJson = normalizedIngredientsJson,
                    responseJson = responseJson
                )
            )
            return freshResponse
        } catch (e: DataIntegrityViolationException) {
            val saved = gptCacheRepository.findByFeatureAndModelAndPromptVersionAndIngredientsHash(
                feature = feature,
                model = model,
                promptVersion = promptVersion,
                ingredientsHash = ingredientsHash
            ) ?: throw e

            saved.lastHitAt = LocalDateTime.now()
            saved.hitCount += 1
            gptCacheRepository.save(saved)

            return objectMapper.readValue(saved.responseJson, responseClass)
        }
    }
}