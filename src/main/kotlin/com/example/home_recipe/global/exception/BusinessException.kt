package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.ResponseCode

class BusinessException (
    val responseCode: ResponseCode,
    message: String = responseCode.message
) : RuntimeException(message)