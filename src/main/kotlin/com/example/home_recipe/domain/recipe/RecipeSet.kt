package com.example.home_recipe.domain.recipe

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recipe_set")
class RecipeSet(

    @Id
    @Column(length = 64)
    val id: String,

    @Column(name = "ingredients_list", nullable = false, columnDefinition = "TEXT")
    val ingredientsList: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val decision: Decision,

    @Column(length = 500)
    val reason: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "recipeSet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val recipeDetails: MutableList<RecipeDetail> = mutableListOf()
) {

    fun addDetail(detail: RecipeDetail) {
        recipeDetails.add(detail)
    }

    enum class Decision {
        COOK, DELIVERY
    }
}
