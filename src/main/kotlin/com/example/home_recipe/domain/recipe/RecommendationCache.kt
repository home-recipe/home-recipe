package com.example.home_recipe.domain.recipe

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recommendation_cache")
class RecommendationCache(

    @Id
    @Column(length = 128)
    val id: String,

    /** 추천 응답 JSON */
    @Column(name = "recommendation_content", nullable = false, columnDefinition = "TEXT")
    val recommendationContent: String,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
