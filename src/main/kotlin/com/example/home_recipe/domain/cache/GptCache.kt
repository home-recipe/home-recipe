package com.example.home_recipe.domain.cache

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "gpt_cache",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_gpt_cache_key",
            columnNames = [
                "feature",
                "model",
                "prompt_version",
                "ingredients_hash"
            ]
        )
    ]
)
class GptCache(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 32)
    val feature: String,

    @Column(nullable = false, length = 64)
    val model: String,

    @Column(name = "prompt_version", nullable = false)
    val promptVersion: Int,

    @Column(
        name = "ingredients_hash",
        nullable = false,
        columnDefinition = "BINARY(32)"
    )
    val ingredientsHash: ByteArray,

    @Column(
        name = "normalized_ingredients_json",
        nullable = false,
        columnDefinition = "JSON"
    )
    val normalizedIngredientsJson: String,

    @Column(
        name = "response_json",
        nullable = false,
        columnDefinition = "JSON"
    )
    val responseJson: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_hit_at")
    var lastHitAt: LocalDateTime? = null,

    @Column(name = "hit_count", nullable = false)
    var hitCount: Long = 0
)