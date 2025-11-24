package com.example.home_recipe.recipe.controller.dto.request


data class ChatRequest(
    val model: String,
    val messages: List<Message>
)
