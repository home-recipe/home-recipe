package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class OpenApiIngredientService(
    @Value("\${external-api.raw-food.service-key}") private val rawFoodKey: String,
    @Value("\${external-api.process-food.service-key}") private val processFoodKey: String,
    @Value("\${external-api.raw-food.url}") private val rawFoodUrl: String,
    @Value("\${external-api.process-food.url}") private val processFoodUrl: String,
    webClientBuilder: WebClient.Builder
) {

    companion object {
        private val log = LoggerFactory.getLogger(OpenApiIngredientService::class.java)

        private const val MAX_CONCURRENT_EXTERNAL_CALLS = 8
        private val semaphore = Semaphore(MAX_CONCURRENT_EXTERNAL_CALLS)

        private const val EXTERNAL_API_TIMEOUT_MS = 800L

        private const val POSITIVE_CACHE_TTL_MINUTES = 10L
        private const val NEGATIVE_CACHE_TTL_MINUTES = 5L

        private const val MAX_POSITIVE_CACHE_SIZE = 10_000L
        private const val MAX_NEGATIVE_CACHE_SIZE = 20_000L

        private val FOOD_SEARCH_LEVELS = listOf("foodNm", "foodLv4Nm", "foodLv5Nm", "foodLv6Nm")
    }

    private val webClient: WebClient = webClientBuilder.build()

    private val positiveCache: Cache<String, List<IngredientResponse>> =
        Caffeine.newBuilder()
            .maximumSize(MAX_POSITIVE_CACHE_SIZE)
            .expireAfterWrite(POSITIVE_CACHE_TTL_MINUTES, TimeUnit.MINUTES)
            .build()

    private val negativeCache: Cache<String, Boolean> =
        Caffeine.newBuilder()
            .maximumSize(MAX_NEGATIVE_CACHE_SIZE)
            .expireAfterWrite(NEGATIVE_CACHE_TTL_MINUTES, TimeUnit.MINUTES)
            .build()

    suspend fun searchExternalFood(keyword: String): List<IngredientResponse> {

        positiveCache.getIfPresent(keyword)?.let {
            log.debug("Positive cache hit - {}", keyword)
            return it
        }

        if (negativeCache.getIfPresent(keyword) == true) {
            log.debug("Negative cache hit - {}", keyword)
            return emptyList()
        }

        val raw = searchLevelsInParallel(keyword, rawFoodKey, rawFoodUrl)
        if (raw.isNotEmpty()) {
            positiveCache.put(keyword, raw)
            negativeCache.invalidate(keyword)
            return raw
        }

        val process = searchLevelsInParallel(keyword, processFoodKey, processFoodUrl)
        if (process.isNotEmpty()) {
            positiveCache.put(keyword, process)
            negativeCache.invalidate(keyword)
            return process
        }

        negativeCache.put(keyword, true)
        positiveCache.invalidate(keyword)

        return emptyList()
    }

    private suspend fun searchLevelsInParallel(
        keyword: String,
        serviceKey: String,
        apiUrl: String
    ): List<IngredientResponse> = coroutineScope {

        val channel = Channel<List<IngredientResponse>>(capacity = Channel.BUFFERED)

        val jobs = FOOD_SEARCH_LEVELS.map { level ->
            async {
                try {
                    val result = callApi(level, keyword, serviceKey, apiUrl)
                    channel.send(result)
                } catch (e: Exception) {
                    log.warn("External API error (degraded to empty) - keyword={}, level={}", keyword, level)
                    channel.send(emptyList())
                }
            }
        }

        repeat(jobs.size) {
            val result = channel.receive()
            if (result.isNotEmpty()) {
                jobs.forEach { it.cancel() }
                return@coroutineScope result
            }
        }

        emptyList()
    }

    private suspend fun callApi(
        paramName: String,
        keyword: String,
        serviceKey: String,
        apiUrl: String
    ): List<IngredientResponse> = semaphore.withPermit {

        val encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8)

        val uri = UriComponentsBuilder
            .fromHttpUrl(apiUrl)
            .queryParam("serviceKey", serviceKey)
            .queryParam("type", "json")
            .queryParam(paramName, encodedKeyword)
            .queryParam("pageNo", 1)
            .queryParam("numOfRows", 5)
            .build(true)
            .toUri()

        val response = webClient.get()
            .uri(uri)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .timeout(Duration.ofMillis(EXTERNAL_API_TIMEOUT_MS))
            .awaitSingle()

        return verifyFoodExistence(response, keyword)
    }

    private fun verifyFoodExistence(
        response: Map<String, Any>,
        keyword: String
    ): List<IngredientResponse> {

        val responseMap = response["response"] as? Map<*, *> ?: return emptyList()
        val header = responseMap["header"] as? Map<*, *>
        val body = responseMap["body"] as? Map<*, *>

        val resultMsg = header?.get("resultMsg") as? String
        if (resultMsg == "NODATA_ERROR") {
            return emptyList()
        }

        val items = body?.get("items") as? List<*>
        if (items.isNullOrEmpty()) {
            return emptyList()
        }

        return listOf(
            IngredientResponse(
                id = null,
                category = null,
                name = keyword,
                source = Source.OPEN_API
            )
        )
    }
}