package com.example.home_recipe.domain.ingredient

import jakarta.persistence.*

@Entity
class IngredientAlias(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val aliasName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    val ingredient: Ingredient
)