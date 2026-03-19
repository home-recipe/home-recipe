package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponseAssembler
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientAlias
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import com.example.home_recipe.repository.IngredientAliasRepository
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.service.recipe.GeminiTextService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IngredientSearchService(
    private val ingredientRepository: IngredientRepository,
    private val ingredientAliasRepository: IngredientAliasRepository,
    private val openApiIngredientService: OpenApiIngredientService,
    private val ingredientGuardrailService: IngredientGuardrailService,
    private val ingredientPromptGenerator: IngredientPromptGenerator,
    private val geminiTextService: GeminiTextService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun search(userInput: String): List<IngredientResponse> {
        val trimmedInput = userInput.trim()

        // 1. 가드레일 체크
        if (!ingredientGuardrailService.isIngredient(trimmedInput)) {
            log.warn("[가드레일 차단] 식재료 유사도 낮음 - 입력: '{}'", trimmedInput)
            throw BusinessException(IngredientCode.NOT_INGREDIENT, HttpStatus.BAD_REQUEST)
        }
        log.info("[가드레일 통과] 입력: '{}'", trimmedInput)

        // 2. Alias DB 검색
        val alias = ingredientAliasRepository.findByAliasName(trimmedInput)
        if (alias != null) {
            log.info("[Alias DB 히트] '{}' -> Ingredient '{}'", trimmedInput, alias.ingredient.name)
            return listOf(IngredientResponseAssembler.toIngredientResponse(alias.ingredient))
        }
        log.info("[Alias DB 미스] '{}'", trimmedInput)

        // 3. Ingredient DB 검색 (LIKE)
        val dbResults = ingredientRepository.findIngredientContainingName(trimmedInput)
        if (dbResults.isNotEmpty()) {
            log.info("[Ingredient DB 히트] '{}' -> {}건", trimmedInput, dbResults.size)
            return IngredientResponseAssembler.toIngredientResponseList(dbResults)
        }
        log.info("[Ingredient DB 미스] '{}'", trimmedInput)

        // 4. 공공데이터 API 검색
        val openApiResults = openApiIngredientService.searchExternalFood(trimmedInput)
        if (openApiResults.isNotEmpty()) {
            log.info("[공공데이터 API 히트] '{}' -> {}건", trimmedInput, openApiResults.size)
            return openApiResults
        }
        log.info("[공공데이터 API 미스] '{}'", trimmedInput)

        // 5. Gemini API 보정 및 자가 학습
        val standardInfo = callGemini(trimmedInput)

        if (!standardInfo.isIngredient) {
            log.warn("[Gemini 판정] '{}' -> 식재료 아님", trimmedInput)
            throw BusinessException(IngredientCode.NOT_INGREDIENT, HttpStatus.BAD_REQUEST)
        }

        log.info("[Gemini 보정] '{}' -> standardName='{}', category='{}'",
            trimmedInput, standardInfo.standardName, standardInfo.category)

        return saveGeminiResult(trimmedInput, standardInfo)
    }

    @Transactional
    fun saveGeminiResult(userInput: String, standardInfo: StandardInfo): List<IngredientResponse> {
        val existingIngredient = ingredientRepository.findByName(standardInfo.standardName)

        val ingredient = if (existingIngredient != null) {
            log.info("[자가 학습] 기존 Ingredient에 Alias 연결 - '{}' -> '{}'",
                userInput, existingIngredient.name)
            existingIngredient
        } else {
            val newIngredient = ingredientRepository.save(
                Ingredient(category = standardInfo.category, name = standardInfo.standardName)
            )
            log.info("[자가 학습] 새 Ingredient 생성 - name='{}', category='{}'",
                newIngredient.name, newIngredient.category)
            newIngredient
        }

        if (userInput != standardInfo.standardName) {
            val existingAlias = ingredientAliasRepository.findByAliasName(userInput)
            if (existingAlias == null) {
                ingredientAliasRepository.save(IngredientAlias(aliasName = userInput, ingredient = ingredient))
                log.info("[자가 학습] 새 Alias 저장 - '{}' -> '{}'", userInput, ingredient.name)
            }
        }

        return listOf(
            IngredientResponse(
                id = ingredient.id,
                category = ingredient.category,
                name = ingredient.name,
                source = Source.GEMINI
            )
        )
    }

    private suspend fun callGemini(userInput: String): StandardInfo {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = ingredientPromptGenerator.generate(userInput)
                geminiTextService.generate(
                    systemPrompt = "당신은 식재료 데이터 전문가입니다.",
                    userPrompt = prompt,
                    responseType = StandardInfo::class.java
                )
            } catch (e: Exception) {
                log.error("[Gemini 호출 실패] 입력: '{}', 에러: {}", userInput, e.message)
                throw BusinessException(IngredientCode.GEMINI_PARSE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}
