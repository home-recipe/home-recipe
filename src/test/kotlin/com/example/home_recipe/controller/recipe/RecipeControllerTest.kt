package com.example.home_recipe.controller.recipe

import com.example.home_recipe.controller.recipe.response.RecipeDecision
import com.example.home_recipe.controller.recipe.response.RecipeDetail
import com.example.home_recipe.controller.recipe.response.RecipesResponse
import com.example.home_recipe.service.recipe.RecipeService
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
class RecipeControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var recipeService: RecipeService

    @Test
    @DisplayName("POST /recipes - 레시피 생성 성공")
    fun getRecipes_success() {
        // given
        val email = "test@example.com"

        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        val auth = JwtAuthenticationToken(jwt, emptyList(), email)

        val response = RecipesResponse(
            decision = RecipeDecision.COOK,
            reason = "냉장고 재료로 충분히 조리 가능",
            recipes = listOf(
                RecipeDetail(
                    recipeName = "김치볶음밥",
                    ingredients = listOf("김치", "밥"),
                    steps = listOf("김치를 볶는다", "밥을 넣고 볶는다")
                )
            )
        )

        whenever(recipeService.chat(email)).thenReturn(response)

        // when & then
        mockMvc.perform(post("/api/recipes").with(authentication(auth)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.response.data.decision").value("COOK"))
            .andExpect(jsonPath("$.response.data.reason").value("냉장고 재료로 충분히 조리 가능"))
            .andExpect(jsonPath("$.response.data.recipes[0].recipeName").value("김치볶음밥"))
            .andExpect(jsonPath("$.response.data.recipes[0].ingredients[0]").value("김치"))
    }
}