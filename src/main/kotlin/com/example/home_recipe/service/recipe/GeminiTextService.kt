package com.example.home_recipe.service.recipe

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GeminiTextService(
    @Value("\${gemini.api-key}") private val apiKey: String,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(GeminiTextService::class.java)

    private val webClient = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .build()

    fun <T> generate(systemPrompt: String, userPrompt: String, responseType: Class<T>): T {
        val requestBody = mapOf(
            "system_instruction" to mapOf(
                "parts" to listOf(mapOf("text" to systemPrompt))
            ),
            "contents" to listOf(
                mapOf(
                    "role" to "user",
                    "parts" to listOf(mapOf("text" to userPrompt))
                )
            ),
            "generationConfig" to mapOf(
                "responseMimeType" to "application/json"
            )
        )

        val response = webClient.post()
            .uri("/v1beta/models/gemini-2.0-flash-exp:generateContent?key=$apiKey")
            .header("Content-Type", "application/json")
            .bodyValue(objectMapper.writeValueAsString(requestBody))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val text = extractText(response)
            ?: throw IllegalStateException("Gemini 응답에서 텍스트를 추출할 수 없습니다")

        return objectMapper.readValue(text, responseType)
    }

    private fun extractText(response: String?): String? {
        if (response == null) return null

        val root = objectMapper.readTree(response)
        val candidates = root.path("candidates")
        if (candidates.isEmpty) return null

        return candidates[0]
            .path("content")
            .path("parts")[0]
            ?.path("text")
            ?.asText()
    }
}
