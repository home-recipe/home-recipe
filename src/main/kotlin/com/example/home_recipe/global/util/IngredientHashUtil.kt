package com.example.home_recipe.global.util

import java.security.MessageDigest

/** 재료 리스트를 정렬·해싱하여 캐시 키를 생성하는 유틸리티 */
object IngredientHashUtil {

    /** 재료 리스트를 가나다순 정렬 후 SHA-256 해싱하여 고유 캐시 키를 반환한다 */
    fun generateCacheKey(ingredients: List<String>): String {
        val sorted = ingredients.sorted()
        val joined = sorted.joinToString(",")
        return sha256(joined)
    }

    /** 접두사를 포함한 캐시 키를 생성한다 (recipe / recommendation 구분용) */
    fun generateCacheKey(prefix: String, ingredients: List<String>): String {
        return "${prefix}_${generateCacheKey(ingredients)}"
    }

    /** 문자열을 SHA-256으로 해싱하여 hex 문자열을 반환한다 */
    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
