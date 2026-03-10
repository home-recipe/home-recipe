package com.example.home_recipe.repository

import com.example.home_recipe.domain.cache.GptCache
import org.springframework.data.jpa.repository.JpaRepository

interface GptCacheRepository : JpaRepository<GptCache, Long> {

    fun findByFeatureAndModelAndPromptVersionAndIngredientsHash(
        feature: String,
        model: String,
        promptVersion: Int,
        ingredientsHash: ByteArray
    ): GptCache?
}