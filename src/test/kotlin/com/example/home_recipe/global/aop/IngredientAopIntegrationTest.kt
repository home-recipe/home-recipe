package com.example.home_recipe.global.aop

import com.example.home_recipe.service.ingredient.IngredientService
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest
@Import(IngredientLogAspect::class)
@ActiveProfiles("test")
class IngredientAopIntegrationTest {

    @Autowired
    lateinit var ingredientService: IngredientService

    @Test
    fun `서비스 메서드 호출 시 AOP 로직이 실행되어야 한다`() = runTest {
        // Given
        val searchName = "소고기"

        // When
        val result = ingredientService.findIngredientsContainingName(searchName)

        // Then
        assertThat(result).isNotNull
    }
}