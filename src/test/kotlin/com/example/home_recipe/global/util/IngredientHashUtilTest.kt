package com.example.home_recipe.global.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class IngredientHashUtilTest {

    @Test
    @DisplayName("동일한 재료 리스트는 항상 같은 해시값을 반환한다")
    fun same_ingredients_same_hash() {
        // given
        val ingredients = listOf("양파", "당근", "감자")

        // when
        val hash1 = IngredientHashUtil.generateCacheKey(ingredients)
        val hash2 = IngredientHashUtil.generateCacheKey(ingredients)

        // then
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    @DisplayName("순서가 다른 동일 재료 리스트도 같은 해시값을 반환한다")
    fun different_order_same_hash() {
        // given
        val ingredients1 = listOf("양파", "당근", "감자")
        val ingredients2 = listOf("감자", "양파", "당근")

        // when
        val hash1 = IngredientHashUtil.generateCacheKey(ingredients1)
        val hash2 = IngredientHashUtil.generateCacheKey(ingredients2)

        // then
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    @DisplayName("재료 구성이 다르면 다른 해시값을 반환한다")
    fun different_ingredients_different_hash() {
        // given
        val ingredients1 = listOf("양파", "당근", "감자")
        val ingredients2 = listOf("양파", "당근", "소고기")

        // when
        val hash1 = IngredientHashUtil.generateCacheKey(ingredients1)
        val hash2 = IngredientHashUtil.generateCacheKey(ingredients2)

        // then
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    @DisplayName("접두사가 다르면 같은 재료여도 다른 캐시 키를 반환한다")
    fun different_prefix_different_key() {
        // given
        val ingredients = listOf("양파", "당근")

        // when
        val recipeKey = IngredientHashUtil.generateCacheKey("recipe", ingredients)
        val recommendationKey = IngredientHashUtil.generateCacheKey("recommendation", ingredients)

        // then
        assertThat(recipeKey).isNotEqualTo(recommendationKey)
        assertThat(recipeKey).startsWith("recipe_")
        assertThat(recommendationKey).startsWith("recommendation_")
    }

    @Test
    @DisplayName("해시값은 SHA-256 형식의 64자리 hex 문자열이다")
    fun hash_format_is_sha256_hex() {
        // given
        val ingredients = listOf("계란", "우유")

        // when
        val hash = IngredientHashUtil.generateCacheKey(ingredients)

        // then
        assertThat(hash).hasSize(64)
        assertThat(hash).matches("[0-9a-f]{64}")
    }

    @Test
    @DisplayName("빈 리스트도 정상적으로 해시값을 생성한다")
    fun empty_list_generates_hash() {
        // given
        val ingredients = emptyList<String>()

        // when
        val hash = IngredientHashUtil.generateCacheKey(ingredients)

        // then
        assertThat(hash).hasSize(64)
    }
}
