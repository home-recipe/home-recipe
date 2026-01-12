package com.example.home_recipe.domain.ingredient

import jakarta.persistence.*

@Entity
@Table(name = "ingredient")
class Ingredient protected constructor() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    lateinit var category: IngredientCategory

    @Column(nullable = false, unique = true, length = 100)
    var name: String = ""

    constructor(category: IngredientCategory, name: String) : this() {
        this.category = category
        this.name = name
    }
}