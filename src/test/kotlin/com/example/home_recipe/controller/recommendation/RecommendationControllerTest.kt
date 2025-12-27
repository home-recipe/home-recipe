package com.example.home_recipe.controller.recommendation

import com.example.home_recipe.controller.recipe.response.RecipeDecision
import com.example.home_recipe.controller.recipe.response.RecipeDetail
import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.controller.recommendation.dto.RecommendationDetail
import com.example.home_recipe.controller.recommendation.dto.RecommendationsResponse
import com.example.home_recipe.service.recipe.RecipeService
import com.example.home_recipe.service.recommendation.RecommendationService
import org.junit.jupiter.api.DisplayName
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RecommendationControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var recommendationService: RecommendationService

    @Test
    @DisplayName("POST /recommendation - 재료 추천 성공")
    fun getRecipes_success() {
        // given
        val email = "test@example.com"

        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        val auth = JwtAuthenticationToken(jwt, emptyList(), email)

        val response = RecommendationsResponse(
            recommendations = listOf(
                RecommendationDetail(
                    recipeName = "김치볶음밥",
                    ingredients = listOf("김치", "밥")
                )
            )
        )

        whenever(recommendationService.chat(email))
            .thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/recommendation")
                .with(authentication(auth))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.response.data.recommendations[0].recipeName")
                .value("김치볶음밥"))
            .andExpect(jsonPath("$.response.data.recommendations[0].ingredients[0]")
                .value("김치"))
    }
}