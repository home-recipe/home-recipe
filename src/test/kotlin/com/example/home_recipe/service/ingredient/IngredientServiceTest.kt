package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.request.CreateIngredientRequest
import com.example.home_recipe.controller.ingredient.dto.request.UpdateIngredientRequest
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import com.example.home_recipe.repository.IngredientRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class IngredientServiceTest {

    @Mock lateinit var ingredientRepository: IngredientRepository

    @InjectMocks
    lateinit var ingredientService: IngredientService

    @Test
    @DisplayName("재료 생성 - 정상")
    fun create_success() {
        // given
        val req = CreateIngredientRequest(
            category = IngredientCategory.VEGETABLE,
            name = "양파"
        )
        doAnswer { inv ->
            (inv.arguments[0] as Ingredient).apply { id = 1L }
        }.whenever(ingredientRepository).save(any())

        // when
        val res = ingredientService.create(req)

        // then
        assertThat(res.category).isEqualTo(IngredientCategory.VEGETABLE)
        assertThat(res.name).isEqualTo("양파")
    }

    @Test
    @DisplayName("재료 생성 - category가 null이면 INGREDIENT_ERROR_013")
    fun create_category_null() {
        // given
        val req = CreateIngredientRequest(
            category = null,
            name = "양파"
        )

        // when & then
        assertThatThrownBy { ingredientService.create(req) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(IngredientCode.INGREDIENT_ERROR_013.message)
    }

    @Test
    @DisplayName("재료 수정 - 정상")
    fun update_success() {
        // given
        val saved = Ingredient(IngredientCategory.FRUIT, "사과").apply { id = 10L }
        whenever(ingredientRepository.findById(10L)).thenReturn(Optional.of(saved))

        val req = UpdateIngredientRequest(
            category = IngredientCategory.MEAT,
            name = "소고기"
        )

        // when
        val res = ingredientService.update(10L, req)

        // then
        assertThat(res.category).isEqualTo(IngredientCategory.MEAT)
        assertThat(res.name).isEqualTo("소고기")
    }

    @Test
    @DisplayName("재료 수정 - 대상이 없으면 INGREDIENT_ERROR_011")
    fun update_not_found() {
        // given
        whenever(ingredientRepository.findById(999L)).thenReturn(Optional.empty())
        val req = UpdateIngredientRequest(
            category = IngredientCategory.SPICE,
            name = "후추"
        )

        // when & then
        assertThatThrownBy { ingredientService.update(999L, req) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(IngredientCode.INGREDIENT_ERROR_011.message)
    }

    @Test
    @DisplayName("재료 수정 - category가 null이면 INGREDIENT_ERROR_013")
    fun update_category_null() {
        // given
        val saved = Ingredient(IngredientCategory.GRAIN, "쌀").apply { id = 7L }
        whenever(ingredientRepository.findById(7L)).thenReturn(Optional.of(saved))
        val req = UpdateIngredientRequest(category = null, name = "찹쌀")

        // when & then
        assertThatThrownBy { ingredientService.update(7L, req) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(IngredientCode.INGREDIENT_ERROR_013.message)
    }

    @Test
    @DisplayName("재료 조회하기 - 단어 검색 시 단어가 포함된 재료를 가져오는지 테스트")
    fun find_ingrdients_containing_name() {
        val grain1 = Ingredient(IngredientCategory.GRAIN, "쌀").apply { id = 7L }
        val grain2 = Ingredient(IngredientCategory.GRAIN, "햅쌀").apply { id = 8L }
        val grain3 = Ingredient(IngredientCategory.GRAIN, "찹쌀").apply { id = 8L }
        val grains = listOf(grain1, grain2, grain3)
        whenever(ingredientRepository.findIngredientContainingName(any())).thenReturn(grains)

        val result = ingredientService.findIngredientsContainingName("쌀")

        Assertions.assertEquals(3, result.size);
    }

    @Test
    @DisplayName("재료 조회하기 - 조회되는 단어가 없다면 빈 리스트 반환하는지 확인")
    fun find_ingrdients_containing_name_isEmpty() {
       val meats = emptyList<Ingredient>()
        whenever(ingredientRepository.findIngredientContainingName(any())).thenReturn(meats)

        val result = ingredientService.findIngredientsContainingName("고기")

        Assertions.assertEquals(0, result.size);
    }
}
