package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.ResponseCode
import org.springframework.http.HttpStatus

class BusinessException(
    val responseCode: ResponseCode,
    val status: HttpStatus,
    message: String = responseCode.message
) : RuntimeException(message)