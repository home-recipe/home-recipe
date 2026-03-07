package com.example.home_recipe.repository

import com.example.home_recipe.domain.recipe.RecommendationCache
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendationCacheRepository : JpaRepository<RecommendationCache, String>
