package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.code.BaseCode
import org.springframework.http.HttpStatus

class BusinessException(
    val baseCode: BaseCode,
    val status: HttpStatus,
    message: String = baseCode.message
) : RuntimeException(message)