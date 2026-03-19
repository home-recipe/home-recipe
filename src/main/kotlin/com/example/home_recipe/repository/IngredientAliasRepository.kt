package com.example.home_recipe.repository

import com.example.home_recipe.domain.ingredient.IngredientAlias
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IngredientAliasRepository : JpaRepository<IngredientAlias, Long> {
    fun findByAliasName(aliasName: String): IngredientAlias?
}
