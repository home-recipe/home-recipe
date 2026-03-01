package com.example.home_recipe.service.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationDetail
import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.domain.recipe.RecommendationCache
import com.example.home_recipe.global.util.IngredientHashUtil
import com.example.home_recipe.repository.RecommendationCacheRepository
import com.example.home_recipe.service.recipe.GeminiTextService
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class RecommendationServiceTest {

    @MockK lateinit var geminiTextService: GeminiTextService
    @MockK lateinit var refrigeratorService: RefrigeratorService
    @MockK lateinit var recommendationCacheRepository: RecommendationCacheRepository

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    private lateinit var recommendationService: RecommendationService

    private val testEmail = "test@example.com"
    private val testIngredients = listOf("양파", "당근", "감자")
    private val testCacheKey = IngredientHashUtil.generateCacheKey("recommendation", testIngredients)

    private val testResponse = RecommendationsResponse(
        recommendations = listOf(
            RecommendationDetail(
                recipeName = "카레라이스",
                ingredients = listOf("카레가루", "밥")
            ),
            RecommendationDetail(
                recipeName = "감자수프",
                ingredients = listOf("우유", "버터")
            )
        )
    )

    @BeforeEach
    fun setUp() {
        recommendationService = RecommendationService(
            geminiTextService, refrigeratorService, recommendationCacheRepository, objectMapper
        )
        every { refrigeratorService.getMyIngredientsOnlyName(testEmail) } returns testIngredients
    }

    @Test
    @DisplayName("캐시 히트 - DB에 데이터가 있으면 AI를 호출하지 않고 캐시된 결과를 반환한다")
    fun cache_hit_returns_cached_result_without_ai_call() {
        // given
        val cachedJson = objectMapper.writeValueAsString(testResponse)
        val cacheEntry = RecommendationCache(id = testCacheKey, recommendationContent = cachedJson)
        every { recommendationCacheRepository.findById(testCacheKey) } returns Optional.of(cacheEntry)

        // when
        val result = recommendationService.chat(testEmail)

        // then
        assertThat(result.recommendations).hasSize(2)
        assertThat(result.recommendations[0].recipeName).isEqualTo("카레라이스")
        assertThat(result.recommendations[1].recipeName).isEqualTo("감자수프")

        verify { geminiTextService wasNot Called }
        verify(exactly = 0) { recommendationCacheRepository.save(any()) }
    }

    @Test
    @DisplayName("캐시 미스 - DB에 데이터가 없으면 Gemini를 호출하고 결과를 저장한다")
    fun cache_miss_calls_gemini_and_saves_to_cache() {
        // given
        every { recommendationCacheRepository.findById(testCacheKey) } returns Optional.empty()
        every { geminiTextService.generate(any(), any(), eq(RecommendationsResponse::class.java)) } returns testResponse

        val savedSlot = slot<RecommendationCache>()
        every { recommendationCacheRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        // when
        val result = recommendationService.chat(testEmail)

        // then
        assertThat(result.recommendations).hasSize(2)
        assertThat(result.recommendations[0].recipeName).isEqualTo("카레라이스")

        verify(exactly = 1) { recommendationCacheRepository.save(any()) }
        assertThat(savedSlot.captured.id).isEqualTo(testCacheKey)

        val savedResponse = objectMapper.readValue(
            savedSlot.captured.recommendationContent, RecommendationsResponse::class.java
        )
        assertThat(savedResponse.recommendations).hasSize(2)
    }

    @Test
    @DisplayName("같은 재료로 재요청하면 캐시에서 반환한다")
    fun second_request_with_same_ingredients_returns_from_cache() {
        // given - 첫 번째 요청: 캐시 미스
        every { recommendationCacheRepository.findById(testCacheKey) } returns Optional.empty()
        every { geminiTextService.generate(any(), any(), eq(RecommendationsResponse::class.java)) } returns testResponse
        val savedSlot = slot<RecommendationCache>()
        every { recommendationCacheRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        recommendationService.chat(testEmail)

        // given - 두 번째 요청: 캐시 히트
        every { recommendationCacheRepository.findById(testCacheKey) } returns Optional.of(savedSlot.captured)

        // when
        val result = recommendationService.chat(testEmail)

        // then
        assertThat(result.recommendations[0].recipeName).isEqualTo("카레라이스")
    }
}
