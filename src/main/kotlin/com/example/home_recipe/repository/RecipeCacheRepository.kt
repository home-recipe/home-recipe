package com.example.home_recipe.repository

import com.example.home_recipe.domain.recipe.RecipeCache
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/** RecipeCache 도큐먼트에 대한 Elasticsearch 리포지토리 */
interface RecipeCacheRepository : ElasticsearchRepository<RecipeCache, String>
