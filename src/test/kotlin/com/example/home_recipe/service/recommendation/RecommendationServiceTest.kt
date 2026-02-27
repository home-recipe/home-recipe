package com.example.home_recipe.service.recommendation

import com.example.home_recipe.controller.recommendation.dto.RecommendationDetail
import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.domain.recipe.RecipeCache
import com.example.home_recipe.global.util.IngredientHashUtil
import com.example.home_recipe.repository.RecipeCacheRepository
import com.example.home_recipe.service.refrigerator.RefrigeratorService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.openai.client.OpenAIClientAsync
import com.openai.models.chat.completions.StructuredChatCompletion
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams
import com.openai.models.chat.completions.StructuredChatCompletionMessage
import com.openai.services.async.ChatServiceAsync
import com.openai.services.async.chat.ChatCompletionServiceAsync
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import java.util.concurrent.CompletableFuture

@ExtendWith(MockKExtension::class)
class RecommendationServiceTest {

    @MockK lateinit var openAiClient: OpenAIClientAsync
    @MockK lateinit var refrigeratorService: RefrigeratorService
    @MockK lateinit var recipeCacheRepository: RecipeCacheRepository

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
            openAiClient, refrigeratorService, recipeCacheRepository, objectMapper
        )
        every { refrigeratorService.getMyIngredientsOnlyName(testEmail) } returns testIngredients
    }

    @Test
    @DisplayName("캐시 히트 - ES에 데이터가 있으면 AI를 호출하지 않고 캐시된 결과를 반환한다")
    fun cache_hit_returns_cached_result_without_ai_call() {
        // given
        val cachedJson = objectMapper.writeValueAsString(testResponse)
        val cacheEntry = RecipeCache(id = testCacheKey, recipeContent = cachedJson)
        every { recipeCacheRepository.findById(testCacheKey) } returns Optional.of(cacheEntry)

        // when
        val result = recommendationService.chat(testEmail)

        // then
        assertThat(result.recommendations).hasSize(2)
        assertThat(result.recommendations[0].recipeName).isEqualTo("카레라이스")
        assertThat(result.recommendations[1].recipeName).isEqualTo("감자수프")

        // AI 호출이 없었는지 검증
        verify { openAiClient wasNot Called }
        verify(exactly = 0) { recipeCacheRepository.save(any()) }
    }

    @Test
    @DisplayName("캐시 미스 - ES에 데이터가 없으면 AI를 호출하고 결과를 ES에 저장한다")
    fun cache_miss_calls_ai_and_saves_to_cache() {
        // given
        every { recipeCacheRepository.findById(testCacheKey) } returns Optional.empty()
        mockOpenAiResponse(testResponse)

        val savedSlot = slot<RecipeCache>()
        every { recipeCacheRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        // when
        val result = recommendationService.chat(testEmail)

        // then
        assertThat(result.recommendations).hasSize(2)
        assertThat(result.recommendations[0].recipeName).isEqualTo("카레라이스")

        // ES에 저장되었는지 검증
        verify(exactly = 1) { recipeCacheRepository.save(any()) }
        assertThat(savedSlot.captured.id).isEqualTo(testCacheKey)

        // 저장된 JSON이 올바른지 검증
        val savedResponse = objectMapper.readValue(
            savedSlot.captured.recipeContent, RecommendationsResponse::class.java
        )
        assertThat(savedResponse.recommendations).hasSize(2)
    }

    @Test
    @DisplayName("같은 재료로 재요청하면 캐시에서 반환한다")
    fun second_request_with_same_ingredients_returns_from_cache() {
        // given - 첫 번째 요청: 캐시 미스
        every { recipeCacheRepository.findById(testCacheKey) } returns Optional.empty()
        mockOpenAiResponse(testResponse)
        val savedSlot = slot<RecipeCache>()
        every { recipeCacheRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        recommendationService.chat(testEmail)

        // given - 두 번째 요청: 캐시 히트
        every { recipeCacheRepository.findById(testCacheKey) } returns Optional.of(savedSlot.captured)

        // when
        val result = recommendationService.chat(testEmail)

        // then
        assertThat(result.recommendations[0].recipeName).isEqualTo("카레라이스")
    }

    /** OpenAI 클라이언트 체인 전체를 모킹한다 */
    @Suppress("UNCHECKED_CAST")
    private fun mockOpenAiResponse(response: RecommendationsResponse) {
        val message = mockk<StructuredChatCompletionMessage<RecommendationsResponse>> {
            every { content() } returns Optional.of(response)
        }
        val choice = mockk<StructuredChatCompletion.Choice<RecommendationsResponse>> {
            every { message() } returns message
        }
        val completion = mockk<StructuredChatCompletion<RecommendationsResponse>> {
            every { choices() } returns listOf(choice)
        }
        val completionsFuture = CompletableFuture.completedFuture(completion)
        val completionsService = mockk<ChatCompletionServiceAsync> {
            every { create(any<StructuredChatCompletionCreateParams<RecommendationsResponse>>()) } returns completionsFuture
        }
        val chatService = mockk<ChatServiceAsync> {
            every { completions() } returns completionsService
        }
        every { openAiClient.chat() } returns chatService
    }
}
