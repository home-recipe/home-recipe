package com.example.home_recipe.controller.dto.ingredient

import com.example.home_recipe.controller.ingredient.dto.request.UpdateIngredientRequest
import com.example.home_recipe.domain.ingredient.IngredientCategory
import jakarta.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UpdateIngredientRequestTest {
    companion object {
        private val factory = Validation.buildDefaultValidatorFactory()
        private val validator = factory.validator

        @JvmStatic @AfterAll
        fun tearDown() {
            factory.close()
        }
    }

    @Test
    @DisplayName("UpdateIngredientRequest - category null이면 INGREDIENT_ERROR_013")
    fun update_category_null_violation() {
        val dto = UpdateIngredientRequest(category = null, name = "파프리카")
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("category")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_013")
        }
    }

    @Test
    @DisplayName("UpdateIngredientRequest - name 빈 문자열이면 INGREDIENT_ERROR_005")
    fun update_name_blank_violation_empty() {
        val dto = UpdateIngredientRequest(category = IngredientCategory.SPICE, name = "")
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("name")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_005")
        }
    }

    @Test
    @DisplayName("UpdateIngredientRequest - name 공백만 있으면 INGREDIENT_ERROR_005")
    fun update_name_blank_violation_spaces() {
        val dto = UpdateIngredientRequest(category = IngredientCategory.SPICE, name = "  ")
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("name")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_005")
        }
    }

    @Test
    @DisplayName("UpdateIngredientRequest - name 길이>100이면 INGREDIENT_ERROR_012")
    fun update_name_length_violation() {
        val longName = "b".repeat(101)
        val dto = UpdateIngredientRequest(category = IngredientCategory.SPICE, name = longName)
        val violations = validator.validate(dto)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("name")
            assertThat(it.message).isEqualTo("INGREDIENT_ERROR_012")
        }
    }

    @Test
    @DisplayName("UpdateIngredientRequest - 정상")
    fun update_ok() {
        val dto = UpdateIngredientRequest(category = IngredientCategory.MEAT, name = "소고기")
        val violations = validator.validate(dto)
        assertThat(violations).isEmpty()
    }
}