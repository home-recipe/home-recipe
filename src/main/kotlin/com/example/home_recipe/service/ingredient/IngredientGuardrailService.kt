package com.example.home_recipe.service.ingredient

import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.store.embedding.CosineSimilarity
import org.springframework.stereotype.Service as SpringService

@SpringService
class IngredientGuardrailService {

    private val embeddingModel: EmbeddingModel = AllMiniLmL6V2EmbeddingModel()

    private val foodAnchor: Embedding = Embedding.from(
        embeddingModel.embed("식재료 요리 재료 음식").content().vector()
    )

    fun isIngredient(userInput: String): Boolean {
        val userVector = Embedding.from(
            embeddingModel.embed(userInput).content().vector()
        )
        val similarity = CosineSimilarity.between(userVector, foodAnchor)
        println("[Guardrail] 입력: '$userInput', 유사도 점수: ${"%.4f".format(similarity)}")
        return similarity > 0.5
    }
}