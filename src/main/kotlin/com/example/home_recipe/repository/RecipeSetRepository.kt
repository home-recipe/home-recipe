package com.example.home_recipe.repository

import com.example.home_recipe.domain.recipe.RecipeSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RecipeSetRepository : JpaRepository<RecipeSet, String> {

    @Query("SELECT rs FROM RecipeSet rs LEFT JOIN FETCH rs.recipeDetails WHERE rs.id = :id")
    fun findByIdWithDetails(id: String): RecipeSet?
}
