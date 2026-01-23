package com.example.home_recipe.controller.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.OpenApiIngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.ingredient.IngredientService
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
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

    @Autowired
    lateinit var ingredientService: IngredientService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private fun funGetAuth(email: String): JwtAuthenticationToken {
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
    fun findIngredients_success() = runTest {
        // given
        val email = "create1@example.com"
        val auth = funGetAuth(email)

        ingredientRepository.saveAndFlush(Ingredient(IngredientCategory.MEAT, "소고기"))
        ingredientRepository.saveAndFlush(Ingredient(IngredientCategory.MEAT, "돼지고기"))
        ingredientRepository.saveAndFlush(Ingredient(IngredientCategory.MEAT, "사슴고기"))
        ingredientRepository.saveAndFlush(Ingredient(IngredientCategory.VEGETABLE, "당근"))

        // 2. When & Then: 호출 및 검증F
        val mvcResult = mockMvc.perform(
            get("/api/ingredients")
                .param("name", "고기")
                .with(authentication(auth))
        )
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.data").isArray)
            .andExpect(jsonPath("$.response.data.length()").value(3))
            .andExpect(
                jsonPath("$.response.data[*].name")
                    .value(org.hamcrest.Matchers.containsInAnyOrder("소고기", "돼지고기", "사슴고기"))
            )
    }

    @Test
    @DisplayName("재료 openApi 조회 성공")
    fun findIngredient_success() = runTest {

        // given
        val email = "create1@example.com"
        val auth = funGetAuth(email)

        ingredientRepository.saveAndFlush(Ingredient(IngredientCategory.ETC, "초콜릿"))
        ingredientRepository.saveAndFlush(Ingredient(IngredientCategory.ETC, "달달한 초콜릿"))

        val mvcResult = mockMvc.perform(
            get("/api/ingredients")
                .param("name", "초콜릿")
                .with(authentication(auth))
        )
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.data").value(hasSize<Any>(2)))
    }

    @Test
    @DisplayName("재료 openApi 조회할 수 없는 경우 빈 리스트 반환")
    fun findIngredient_fail() {
        // given
        val email = "create1@example.com"
        val auth = funGetAuth(email)

        val mvcResult = mockMvc.perform(
            get("/api/ingredients")
                .param("name", "가방")
                .with(authentication(auth))
        )
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andDo(print())
            .andExpect(jsonPath("$.response.data").value(hasSize<Any>(0)))
    }
}