package com.example.home_recipe.controller.refrigerator

import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.RefrigeratorRepository
import com.example.home_recipe.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RefrigeratorControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var ingredientRepository: IngredientRepository

    @Autowired
    lateinit var refrigeratorRepository: RefrigeratorRepository

    @Test
    @DisplayName("POST /refrigerator - 냉장고 없으면 생성 후 201")
    fun create_whenAbsent_created() {
        // given
        val email = "create1@example.com"
        userRepository.save(User(email = email, password = "pw", name = "me"))

        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        val auth = JwtAuthenticationToken(jwt, emptyList(), email)

        // when & then
        mockMvc.perform(post("/refrigerator").with(authentication(auth)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.response.data").value(true))

        // then DB 확인
        val u = userRepository.findByEmail(email).orElseThrow()
        assertThat(u.hasRefrigerator()).isTrue()
    }

    @Test
    @DisplayName("PUT /refrigerator/ingredient/{id} - 재료 추가")
    fun addIngredient_success() {
        val email = "add1@example.com"
        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        val auth = JwtAuthenticationToken(jwt, emptyList(), email)

        userRepository.save(User(email = email, password = "pw", name = "me"))
        mockMvc.perform(post("/refrigerator").with(authentication(auth))).andExpect(status().isCreated)

        val ing = ingredientRepository.save(Ingredient(IngredientCategory.VEGETABLE, "양파"))

        mockMvc.perform(put("/refrigerator/ingredient/{id}", ing.id!!).with(authentication(auth)))
            .andExpect(status().isOk)

        val u = userRepository.findByEmail(email).orElseThrow()
        assertThat(u.refrigeratorExternal.ingredients.any { it.id == ing.id }).isTrue()
    }

    @Test
    @DisplayName("DELETE /refrigerator/ingredient/{id} - 재료 사용")
    fun useIngredient_success() {
        val email = "use1@example.com"
        userRepository.save(User(email = email, password = "pw", name = "me"))
        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        val auth = JwtAuthenticationToken(jwt, emptyList(), email)

        mockMvc.perform(post("/refrigerator").with(authentication(auth))).andExpect(status().isCreated)

        val ing = ingredientRepository.save(Ingredient(IngredientCategory.VEGETABLE, "당근"))
        mockMvc.perform(put("/refrigerator/ingredient/{id}", ing.id!!).with(authentication(auth)))
            .andExpect(status().isOk)

        mockMvc.perform(delete("/refrigerator/ingredient/{id}", ing.id!!).with(authentication(auth)))
            .andExpect(status().isOk)

        val u = userRepository.findByEmail(email).orElseThrow()
        assertThat(u.refrigeratorExternal.ingredients.any { it.id == ing.id }).isFalse()
    }
}