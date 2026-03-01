package com.example.home_recipe.service.recipe

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class GeminiImageService(
    @Value("\${gemini.api-key}") private val apiKey: String,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(GeminiImageService::class.java)

    private val webClient = WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com")
        .build()

    fun generateImage(recipeName: String): ByteArray? {
        return try {
            val requestBody = mapOf(
                "contents" to listOf(
                    mapOf(
                        "parts" to listOf(
                            mapOf("text" to "${recipeName}의 완성된 요리 사진을 생성해줘. 먹음직스럽고 현실적인 자취생 요리 스타일로.")
                        )
                    )
                ),
                "generationConfig" to mapOf(
                    "responseModalities" to listOf("TEXT", "IMAGE")
                )
            )

            val response = webClient.post()
                .uri("/v1/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .header("Content-Type", "application/json")
                .bodyValue(objectMapper.writeValueAsString(requestBody))
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            extractImageBytes(response)
        } catch (e: Exception) {
            log.error("Gemini 이미지 생성 실패: recipeName={}, error={}", recipeName, e.message)
            null
        }
    }

    private fun extractImageBytes(response: String?): ByteArray? {
        if (response == null) return null

        val root = objectMapper.readTree(response)
        val candidates = root.path("candidates")
        if (candidates.isEmpty) return null

        val parts = candidates[0].path("content").path("parts")
        for (part in parts) {
            val inlineData = part.path("inlineData")
            if (!inlineData.isMissingNode) {
                val base64Data = inlineData.path("data").asText()
                return Base64.getDecoder().decode(base64Data)
            }
        }
        return null
    }
}
