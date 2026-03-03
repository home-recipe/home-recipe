package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
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

        private const val API_NO_DATA_MSG = "NODATA_ERROR"

        private val FOOD_SEARCH_LEVELS =
            listOf("foodNm", "foodLv4Nm", "foodLv5Nm", "foodLv6Nm")
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

    private val inFlight =
        ConcurrentHashMap<String, CompletableDeferred<List<IngredientResponse>>>()

    private fun normalizeKeyword(keyword: String): String =
        keyword.trim().replace(Regex("\\s+"), " ").lowercase()

    suspend fun searchExternalFood(keyword: String): List<IngredientResponse> {

        val cacheKey = normalizeKeyword(keyword)

        positiveCache.getIfPresent(cacheKey)?.let { return it }
        if (negativeCache.getIfPresent(cacheKey) == true) return emptyList()

        val myDeferred = CompletableDeferred<List<IngredientResponse>>()
        val existing = inFlight.putIfAbsent(cacheKey, myDeferred)

        if (existing != null) return existing.await()

        return try {

            val targets = buildTargets()
            val result = searchTargetsInParallel(cacheKey, targets)

            if (result.isNotEmpty()) {
                positiveCache.put(cacheKey, result)
                negativeCache.invalidate(cacheKey)
            } else {
                negativeCache.put(cacheKey, true)
            }

            myDeferred.complete(result)
            result

        } finally {
            inFlight.remove(cacheKey, myDeferred)
        }
    }

    private data class Target(
        val apiUrl: String,
        val serviceKey: String,
        val level: String
    )

    private fun buildTargets(): List<Target> {
        val raw = FOOD_SEARCH_LEVELS.map {
            Target(rawFoodUrl, rawFoodKey, it)
        }
        val process = FOOD_SEARCH_LEVELS.map {
            Target(processFoodUrl, processFoodKey, it)
        }
        return raw + process
    }

    private suspend fun searchTargetsInParallel(
        keyword: String,
        targets: List<Target>
    ): List<IngredientResponse> = coroutineScope {

        val channel = Channel<List<IngredientResponse>>(Channel.BUFFERED)

        val jobs = targets.map { target ->
            async {
                try {
                    val result = callApi(
                        target.level,
                        keyword,
                        target.serviceKey,
                        target.apiUrl
                    )
                    channel.send(result)
                } catch (e: Exception) {
                    log.warn("External API error (degraded) - {}", keyword)
                    channel.send(emptyList())
                }
            }
        }

        try {
            repeat(jobs.size) {
                val result = channel.receive()
                if (result.isNotEmpty()) {
                    jobs.forEach { it.cancel() }
                    return@coroutineScope result
                }
            }
            emptyList()
        } finally {
            channel.close()
            jobs.forEach { it.cancel() }
        }
    }

    private suspend fun callApi(
        paramName: String,
        keyword: String,
        serviceKey: String,
        apiUrl: String
    ): List<IngredientResponse> = semaphore.withPermit {

        val encodedKeyword =
            URLEncoder.encode(keyword, StandardCharsets.UTF_8)

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
            .bodyToMono(OpenApiFoodResponse::class.java)
            .timeout(Duration.ofMillis(EXTERNAL_API_TIMEOUT_MS))
            .awaitSingle()

        return verifyFoodExistence(response, keyword)
    }

    private fun verifyFoodExistence(
        response: OpenApiFoodResponse,
        keyword: String
    ): List<IngredientResponse> {

        val wrapper = response.response ?: return emptyList()

        if (wrapper.header?.resultMsg == API_NO_DATA_MSG)
            return emptyList()

        val items = wrapper.body?.items

        val isEmpty = when (items) {
            null -> true
            is Collection<*> -> items.isEmpty()
            is Map<*, *> -> items.isEmpty()
            else -> false
        }

        if (isEmpty) return emptyList()

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