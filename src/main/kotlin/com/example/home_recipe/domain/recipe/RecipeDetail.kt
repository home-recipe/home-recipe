package com.example.home_recipe.domain.recipe

import jakarta.persistence.*

@Entity
@Table(name = "recipe_detail")
class RecipeDetail(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    val recipeSet: RecipeSet,

    @Column(name = "recipe_name", nullable = false, length = 255)
    val recipeName: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val ingredients: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val steps: String,

    @Column(name = "image_url", length = 2048)
    var imageUrl: String? = null
)
