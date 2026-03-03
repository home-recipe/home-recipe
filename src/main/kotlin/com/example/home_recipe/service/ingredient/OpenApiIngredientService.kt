package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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

        private const val LEVEL_FOOD_NM = "foodNm"
        private const val LEVEL_FOOD_LV4_NM = "foodLv4Nm"
        private const val LEVEL_FOOD_LV5_NM = "foodLv5Nm"
        private const val LEVEL_FOOD_LV6_NM = "foodLv6Nm"

        private val FOOD_SEARCH_LEVELS = listOf(
            LEVEL_FOOD_NM,
            LEVEL_FOOD_LV4_NM,
            LEVEL_FOOD_LV5_NM,
            LEVEL_FOOD_LV6_NM
        )

        private const val QUERY_SERVICE_KEY = "serviceKey"
        private const val QUERY_TYPE = "type"
        private const val QUERY_PAGE_NO = "pageNo"
        private const val QUERY_NUM_OF_ROWS = "numOfRows"

        private const val RESPONSE_TYPE = "json"
        private const val DEFAULT_PAGE_NO = 1
        private const val DEFAULT_NUM_OF_ROWS = 5

        private const val API_NO_DATA_MSG = "NODATA_ERROR"

        private const val KEY_RESPONSE = "response"
        private const val KEY_HEADER = "header"
        private const val KEY_BODY = "body"
        private const val KEY_ITEMS = "items"
        private const val KEY_RESULT_MSG = "resultMsg"
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

    private data class SearchTarget(
        val apiUrl: String,
        val serviceKey: String,
        val level: String,
        val sourceName: String
    )

    suspend fun searchExternalFood(keyword: String): List<IngredientResponse> {
        val cacheKey = keyword

        positiveCache.getIfPresent(cacheKey)?.let {
            log.debug("Positive cache hit - {}", cacheKey)
            return it
        }
        if (negativeCache.getIfPresent(cacheKey) == true) {
            log.debug("Negative cache hit - {}", cacheKey)
            return emptyList()
        }

        val targets = buildTargets()

        val result = searchTargetsInParallel(keyword, targets)
        if (result.isNotEmpty()) {
            positiveCache.put(cacheKey, result)
            negativeCache.invalidate(cacheKey)
            return result
        }

        negativeCache.put(cacheKey, true)
        positiveCache.invalidate(cacheKey)
        return emptyList()
    }

    private fun buildTargets(): List<SearchTarget> {
        val rawTargets = FOOD_SEARCH_LEVELS.map { level ->
            SearchTarget(
                apiUrl = rawFoodUrl,
                serviceKey = rawFoodKey,
                level = level,
                sourceName = "raw"
            )
        }
        val processTargets = FOOD_SEARCH_LEVELS.map { level ->
            SearchTarget(
                apiUrl = processFoodUrl,
                serviceKey = processFoodKey,
                level = level,
                sourceName = "process"
            )
        }
        return rawTargets + processTargets
    }

    private suspend fun searchTargetsInParallel(
        keyword: String,
        targets: List<SearchTarget>
    ): List<IngredientResponse> = coroutineScope {

        val channel = Channel<Pair<SearchTarget, List<IngredientResponse>>>(capacity = Channel.BUFFERED)

        val jobs = targets.map { target ->
            async {
                try {
                    val result = callApi(
                        paramName = target.level,
                        keyword = keyword,
                        serviceKey = target.serviceKey,
                        apiUrl = target.apiUrl
                    )
                    channel.send(target to result)
                } catch (e: Exception) {
                    // Commit 7a 정책 유지: 외부 오류는 empty로 degrade
                    log.warn(
                        "External API error (degraded to empty) - keyword={}, source={}, level={}",
                        keyword, target.sourceName, target.level
                    )
                    channel.send(target to emptyList())
                }
            }
        }

        try {
            repeat(jobs.size) {
                val (target, result) = channel.receive()
                if (result.isNotEmpty()) {
                    log.info(
                        "OpenAPI hit -> early return - keyword={}, source={}, level={}",
                        keyword, target.sourceName, target.level
                    )
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

        val encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8)

        val uri = UriComponentsBuilder
            .fromHttpUrl(apiUrl)
            .queryParam(QUERY_SERVICE_KEY, serviceKey)
            .queryParam(QUERY_TYPE, RESPONSE_TYPE)
            .queryParam(paramName, encodedKeyword)
            .queryParam(QUERY_PAGE_NO, DEFAULT_PAGE_NO)
            .queryParam(QUERY_NUM_OF_ROWS, DEFAULT_NUM_OF_ROWS)
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

        val responseMap = response[KEY_RESPONSE] as? Map<*, *> ?: return emptyList()
        val header = responseMap[KEY_HEADER] as? Map<*, *>
        val body = responseMap[KEY_BODY] as? Map<*, *>

        val resultMsg = header?.get(KEY_RESULT_MSG) as? String
        if (resultMsg == API_NO_DATA_MSG) {
            return emptyList()
        }

        val items = body?.get(KEY_ITEMS) as? List<*>
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