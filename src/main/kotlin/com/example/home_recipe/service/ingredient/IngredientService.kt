package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.request.CreateIngredientRequest
import com.example.home_recipe.controller.ingredient.dto.request.UpdateIngredientRequest
import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponseAssembler
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
        return IngredientResponseAssembler.toIngredientResponse(saved)
    }

    @Transactional
    fun createAll(requests: List<CreateIngredientRequest>): List<IngredientResponse> {
        val entities = requests.map { req ->
            val category = req.category
                ?: throw BusinessException(IngredientCode.INGREDIENT_ERROR_013, HttpStatus.BAD_REQUEST)
            Ingredient(category = category, name = req.name)
        }
        val saved = ingredientRepository.saveAll(entities)
        return IngredientResponseAssembler.toIngredientResponseList(saved)
    }

    @Transactional
    fun update(id: Long, request: UpdateIngredientRequest): IngredientResponse {
        val ingredient = ingredientRepository.findById(id)
            .orElseThrow { BusinessException(IngredientCode.INGREDIENT_ERROR_011, HttpStatus.BAD_REQUEST) }

        val category = request.category
            ?: throw BusinessException(IngredientCode.INGREDIENT_ERROR_013, HttpStatus.BAD_REQUEST)

        ingredient.category = category
        ingredient.name = request.name

        return IngredientResponseAssembler.toIngredientResponse(ingredient)
    }

    @Transactional(readOnly = true)
    fun findIngredientsContainingName(name: String): List<IngredientResponse> {
        val ingredients = ingredientRepository.findIngredientContainingName(name)
        return IngredientResponseAssembler.toIngredientResponseList(ingredients)
    }
}


