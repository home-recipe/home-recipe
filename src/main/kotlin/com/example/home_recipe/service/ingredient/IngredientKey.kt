package com.example.home_recipe.service.ingredient

import java.security.MessageDigest

object IngredientKey {

    fun normalize(ingredients: List<String>): List<String> {
        return ingredients.asSequence()
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .toList()
    }

    fun ingredientsHash(normalizedIngredients: List<String>): ByteArray {
        val canonical = normalizedIngredients.joinToString("|")
        return sha256(canonical)
    }

    private fun sha256(value: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(value.toByteArray(Charsets.UTF_8))
    }
}