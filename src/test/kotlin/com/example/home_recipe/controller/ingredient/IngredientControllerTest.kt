package com.example.home_recipe.controller.ingredient

import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.ingredient.IngredientService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class IngredientControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var ingredientRepository: IngredientRepository

    @Autowired
    lateinit var userRepository: UserRepository

    private fun funGetAuth(email : String) : JwtAuthenticationToken {
        userRepository.save(User(email = email, password = "pw", name = "me"))
        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        return JwtAuthenticationToken(jwt, emptyList(), email)
    }


    @Test
    @DisplayName("재료 조회하기 성공")
    fun findIngredients_success() {
        // given
        val email = "create1@example.com"
        val auth = funGetAuth(email)

        ingredientRepository.save(Ingredient(IngredientCategory.MEAT, "소고기"))
        ingredientRepository.save(Ingredient(IngredientCategory.MEAT, "돼지고기"))
        ingredientRepository.save(Ingredient(IngredientCategory.MEAT, "사슴고기"))
        ingredientRepository.save(Ingredient(IngredientCategory.VEGETABLE, "당근"))

        // 2. When & Then: 호출 및 검증
        mockMvc.perform(
            get("/api/ingredients/ingredient")
                .param("name", "고기")
                .with(authentication(auth))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.data").isArray)
            .andExpect(jsonPath("$.response.data.length()").value(3))
            .andExpect(
                jsonPath("$.response.data[*].name")
                    .value(org.hamcrest.Matchers.containsInAnyOrder("소고기", "돼지고기", "사슴고기"))
            )
    }
}