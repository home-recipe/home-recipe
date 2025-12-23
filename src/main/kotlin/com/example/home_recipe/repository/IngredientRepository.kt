package com.example.home_recipe.repository

import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByCategoryAndName(category: IngredientCategory, name: String): Ingredient?

    @Query("""
        select i from Ingredient i where i.name LIKE %:name%
    """)
    fun findByNameContaining(@Param("name") name: String): List<Ingredient>
}