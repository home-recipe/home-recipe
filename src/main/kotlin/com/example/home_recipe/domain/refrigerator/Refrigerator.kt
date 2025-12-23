package com.example.home_recipe.domain.refrigerator

import jakarta.persistence.*
import com.example.home_recipe.domain.ingredient.Ingredient

@Entity
@Table(name = "refrigerator")
class Refrigerator protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fridges_ingredients",
        joinColumns = [JoinColumn(name = "refrigerator_id")],
        inverseJoinColumns = [JoinColumn(name = "ingredient_id")],
        uniqueConstraints = [UniqueConstraint(
            name = "uk_fridge_ingredient",
            columnNames = ["refrigerator_id", "ingredient_id"]
        )]
    )
    val ingredients: MutableSet<Ingredient> = mutableSetOf()

    companion object {
        fun create(defaultIngredients: Collection<Ingredient> = emptyList()): Refrigerator =
            Refrigerator().apply {
                defaultIngredients.forEach(::addIngredient)
            }
    }

    fun addIngredient(ingredient: Ingredient): Boolean =
        ingredients.add(ingredient)

    fun useIngredient(ingredient: Ingredient): Boolean =
        ingredients.remove(ingredient)

    fun removeIngredientById(ingredientId: Long): Boolean {
        val target = ingredients.firstOrNull { it.id == ingredientId } ?: return false
        return ingredients.remove(target)
    }

    fun ingredientNames(): List<String> =
        ingredients.map { it.name }

    fun hasIngredient(ingredient: Ingredient): Boolean =
        ingredient in ingredients
}
