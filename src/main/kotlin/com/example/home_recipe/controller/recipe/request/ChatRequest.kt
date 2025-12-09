package com.example.home_recipe.controller.recipe.request

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)