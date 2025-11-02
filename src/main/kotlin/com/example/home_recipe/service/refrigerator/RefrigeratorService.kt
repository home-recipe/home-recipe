package com.example.home_recipe.service.refrigerator

import com.example.home_recipe.domain.refrigerator.Refrigerator
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.RefrigeratorRepository
import com.example.home_recipe.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RefrigeratorService(
    private val refrigeratorRepository: RefrigeratorRepository,
    private val ingredientRepository: IngredientRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createForUser(email: String): Refrigerator {
    }

    @Transactional
    fun addIngredient(email: String, ingredientId: Long): Boolean {
    }

    @Transactional
    fun useIngredient(email: String, ingredientId: Long): Boolean {
    }
}
