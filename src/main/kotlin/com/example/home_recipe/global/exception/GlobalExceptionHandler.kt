package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.error(
            responseCode = ex.baseCode,
            status = ex.status
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val error = ex.bindingResult.fieldErrors.firstOrNull()
        val codeName = error?.defaultMessage

        val allCodes = listOf<BaseCode>(
            *UserCode.entries.toTypedArray(),
            *RefrigeratorCode.entries.toTypedArray(),
            *RecipeCode.entries.toTypedArray(),
            *IngredientCode.entries.toTypedArray(),
            *RecommendationCode.entries.toTypedArray(),
            *CommonCode.entries.toTypedArray(),
        )

        val responseCode = allCodes.find { it.code == codeName } ?: CommonCode.VALIDATION_ERROR

        return ApiResponse.error(
            responseCode = responseCode,
            status = HttpStatus.BAD_REQUEST
        )
    }
}