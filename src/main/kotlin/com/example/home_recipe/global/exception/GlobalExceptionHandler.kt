package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.ResponseCode
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
            responseCode = ex.responseCode,
            status = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val error = ex.bindingResult.fieldErrors.firstOrNull()
        val codeName = error?.defaultMessage ?: ResponseCode.SIGNUP_ERROR_011.name
        val responseCode = ResponseCode.entries.find { it.name == codeName } ?: ResponseCode.SIGNUP_ERROR_011

        return ApiResponse.error(
            responseCode = responseCode,
            status = HttpStatus.BAD_REQUEST
        )
    }
}