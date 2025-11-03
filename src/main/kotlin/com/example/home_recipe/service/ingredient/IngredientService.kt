package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.dto.DtoMapper
import com.example.home_recipe.controller.dto.ingredient.CreateIngredientRequest
import com.example.home_recipe.controller.dto.ingredient.IngredientResponse
import com.example.home_recipe.controller.dto.ingredient.UpdateIngredientRequest
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import com.example.home_recipe.repository.IngredientRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IngredientService(
    private val ingredientRepository: IngredientRepository
) {

    @Transactional
    fun create(request: CreateIngredientRequest): IngredientResponse {
        val category = request.category
            ?: throw BusinessException(IngredientCode.INGREDIENT_ERROR_013, HttpStatus.BAD_REQUEST)

        val entity = Ingredient(category = category, name = request.name)
        val saved = ingredientRepository.save(entity)
        return DtoMapper.toIngredientResponse(saved)
    }

    @Transactional
    fun update(id: Long, request: UpdateIngredientRequest): IngredientResponse {
        val ingredient = ingredientRepository.findById(id)
            .orElseThrow { BusinessException(IngredientCode.INGREDIENT_ERROR_011, HttpStatus.BAD_REQUEST) }

        val category = request.category
            ?: throw BusinessException(IngredientCode.INGREDIENT_ERROR_013, HttpStatus.BAD_REQUEST)

        ingredient.category = category
        ingredient.name = request.name
        // 더티체킹으로 반영
        return DtoMapper.toIngredientResponse(ingredient)
    }
}


