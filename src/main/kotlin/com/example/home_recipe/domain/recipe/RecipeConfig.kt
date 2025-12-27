package com.example.home_recipe.domain.recipe


import com.openai.client.OpenAIClientAsync
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAiConfig(
    @Value("\${openai.api-key}") private val apiKey: String
) {

    @Bean
    fun openAiClient(): OpenAIClientAsync =
        OpenAIOkHttpClientAsync.builder()
            .apiKey(apiKey)
            .build()
}