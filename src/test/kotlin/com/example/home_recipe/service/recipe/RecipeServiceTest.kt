package com.example.home_recipe.service.recipe

//import com.example.home_recipe.controller.recipe.response.RecipeDecision
//import com.example.home_recipe.controller.recipe.response.RecipeDetailResponse
//import com.example.home_recipe.controller.recipe.response.RecipesResponse
//import com.example.home_recipe.domain.recipe.RecipeDetail
//import com.example.home_recipe.domain.recipe.RecipeSet
//import com.example.home_recipe.global.util.IngredientHashUtil
//import com.example.home_recipe.repository.RecipeSetRepository
//import com.example.home_recipe.service.refrigerator.RefrigeratorService
//import com.example.home_recipe.service.storage.ImageStorageService
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import io.mockk.*
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//
//@ExtendWith(MockKExtension::class)
//class RecipeServiceTest {
//
//    @MockK lateinit var geminiTextService: GeminiTextService
//    @MockK lateinit var refrigeratorService: RefrigeratorService
//    @MockK lateinit var recipeSetRepository: RecipeSetRepository
//    @MockK lateinit var geminiImageService: GeminiImageService
//    @MockK lateinit var imageStorageService: ImageStorageService
//
//    private val objectMapper: ObjectMapper = jacksonObjectMapper()
//
//    private lateinit var recipeService: RecipeService
//
//    private val testEmail = "test@example.com"
//    private val testIngredients = listOf("양파", "당근", "감자")
//    private val testCacheKey = IngredientHashUtil.generateCacheKey(testIngredients)
//
//    private val testResponse = RecipesResponse(
//        decision = RecipeDecision.COOK,
//        reason = "재료가 충분하다",
//        recipes = listOf(
//            RecipeDetailResponse(
//                recipeName = "감자양파볶음",
//                ingredients = listOf("감자 2개", "양파 1개", "당근 반개"),
//                steps = listOf("1단계(손질): 재료를 썰어라", "2단계(볶기): 팬에 볶아라")
//            )
//        )
//    )
//
//    @BeforeEach
//    fun setUp() {
//        recipeService = RecipeService(
//            geminiTextService, refrigeratorService, recipeSetRepository,
//            objectMapper, geminiImageService, imageStorageService
//        )
//        every { refrigeratorService.getMyIngredientsOnlyName(testEmail) } returns testIngredients
//    }
//
//    @Test
//    @DisplayName("캐시 히트 - DB에 데이터가 있으면 AI를 호출하지 않고 캐시된 결과를 반환한다")
//    fun cache_hit_returns_cached_result_without_ai_call() {
//        // given
//        val cachedSet = buildRecipeSet()
//        every { recipeSetRepository.findByIdWithDetails(testCacheKey) } returns cachedSet
//
//        // when
//        val result = recipeService.chat(testEmail)
//
//        // then
//        assertThat(result.decision).isEqualTo(RecipeDecision.COOK)
//        assertThat(result.recipes).hasSize(1)
//        assertThat(result.recipes[0].recipeName).isEqualTo("감자양파볶음")
//
//        verify { geminiTextService wasNot Called }
//        verify(exactly = 0) { recipeSetRepository.save(any()) }
//    }
//
//    @Test
//    @DisplayName("캐시 미스 - DB에 데이터가 없으면 Gemini를 호출하고 결과를 저장한다")
//    fun cache_miss_calls_gemini_and_saves_to_db() {
//        // given
//        every { recipeSetRepository.findByIdWithDetails(testCacheKey) } returns null
//        every { geminiTextService.generate(any(), any(), eq(RecipesResponse::class.java)) } returns testResponse
//        every { geminiImageService.generateImage(any()) } returns null
//        every { recipeSetRepository.save(any<RecipeSet>()) } answers { firstArg() }
//
//        // when
//        val result = recipeService.chat(testEmail)
//
//        // then
//        assertThat(result.decision).isEqualTo(RecipeDecision.COOK)
//        assertThat(result.recipes[0].recipeName).isEqualTo("감자양파볶음")
//
//        verify(exactly = 1) { geminiTextService.generate(any(), any(), eq(RecipesResponse::class.java)) }
//        verify(exactly = 1) { recipeSetRepository.save(any<RecipeSet>()) }
//    }
//
//    private fun buildRecipeSet(): RecipeSet {
//        val recipeSet = RecipeSet(
//            id = testCacheKey,
//            ingredientsList = testIngredients.sorted().joinToString(","),
//            decision = RecipeSet.Decision.COOK,
//            reason = "재료가 충분하다"
//        )
//        val detail = RecipeDetail(
//            recipeSet = recipeSet,
//            recipeName = "감자양파볶음",
//            ingredients = objectMapper.writeValueAsString(listOf("감자 2개", "양파 1개", "당근 반개")),
//            steps = objectMapper.writeValueAsString(listOf("1단계(손질): 재료를 썰어라", "2단계(볶기): 팬에 볶아라")),
//            imageUrl = null
//        )
//        recipeSet.addDetail(detail)
//        return recipeSet
//    }
//}
