package com.example.home_recipe.controller.dto.ingredient

import com.example.home_recipe.domain.ingredient.IngredientCategory
import jakarta.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class CreateIngredientRequestTest {
    companion object {
        private val factory = Validation.buildDefaultValidatorFactory()
        private val validator = factory.validator

        @JvmStatic @AfterAll
        fun tearDown() {
            factory.close()
        }
    }

    @Test
    @DisplayName("CreateIngredientRequest - category null이면 INGREDIENT_ERROR_013")
    fun create_category_null_violation() {
        // given
        val dto = CreateIngredientRequest(
            category = null, // @NotNull
            name = "양파"
        )

        // when
        val violations = validator.validate(dto)

        // then
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("category")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_013")
        }
    }

    @Test
    @DisplayName("CreateIngredientRequest - 정상")
    fun create_ok() {
        val dto = CreateIngredientRequest(
            category = IngredientCategory.VEGETABLE,
            name = "양파"
        )
        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }

    @Test
    @DisplayName("CreateIngredientRequest - name이 빈 문자열이면 INGREDIENT_ERROR_005")
    fun create_name_blank_violation_empty() {
        val dto = CreateIngredientRequest(category = IngredientCategory.VEGETABLE, name = "")
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("name")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_005")
        }
    }

    @Test
    @DisplayName("CreateIngredientRequest - name이 공백만 있으면 INGREDIENT_ERROR_005")
    fun create_name_blank_violation_spaces() {
        val dto = CreateIngredientRequest(category = IngredientCategory.VEGETABLE, name = "   ")
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("name")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_005")
        }
    }

    @Test
    @DisplayName("CreateIngredientRequest - name 길이>100이면 INGREDIENT_ERROR_012")
    fun create_name_length_violation() {
        val longName = "a".repeat(101)
        val dto = CreateIngredientRequest(category = IngredientCategory.VEGETABLE, name = longName)
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("name")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_012")
        }
    }
}