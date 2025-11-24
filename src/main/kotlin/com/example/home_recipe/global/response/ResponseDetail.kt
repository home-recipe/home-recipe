package com.example.home_recipe.global.response

data class ResponseDetail<T> (
    val code: String,
    val data: T? = null
)