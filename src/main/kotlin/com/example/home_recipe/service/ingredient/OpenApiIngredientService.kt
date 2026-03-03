package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentLinkedQueue

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
        private val externalCallSemaphore = Semaphore(MAX_CONCURRENT_EXTERNAL_CALLS)

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

    suspend fun searchExternalFood(keyword: String): List<IngredientResponse> {
        val rawResult = searchLevelsInParallel(keyword, rawFoodKey, rawFoodUrl)
        if (rawResult.isNotEmpty()) return rawResult

        return searchLevelsInParallel(keyword, processFoodKey, processFoodUrl)
    }

    private suspend fun searchLevelsInParallel(
        keyword: String,
        serviceKey: String,
        apiUrl: String
    ): List<IngredientResponse> = coroutineScope {
        val errors = ConcurrentLinkedQueue<Throwable>()
        val channel = Channel<Pair<String, List<IngredientResponse>>>(capacity = Channel.BUFFERED)

        val jobs = FOOD_SEARCH_LEVELS.map { level ->
            async {
                try {
                    val result = callApiWithParam(level, keyword, serviceKey, apiUrl)
                    channel.send(level to result)
                } catch (e: Exception) {
                    errors.add(e)
                    log.error("OpenAPI 호출 실패 - apiUrl: {}, level: {}, keyword: {}", apiUrl, level, keyword, e)
                    channel.send(level to emptyList())
                }
            }
        }

        try {
            repeat(jobs.size) {
                val (level, result) = channel.receive()
                if (result.isNotEmpty()) {
                    log.info("OpenAPI 검색 성공(조기 종료) - apiUrl: {}, level: {}, keyword: {}", apiUrl, level, keyword)
                    jobs.forEach { it.cancel() }
                    return@coroutineScope result
                }
            }

            if (errors.isNotEmpty()) {
                throw BusinessException(IngredientCode.OPEN_API_INGREDIENT_ERROR_01, HttpStatus.INTERNAL_SERVER_ERROR)
            }

            emptyList()
        } finally {
            channel.close()
            jobs.forEach { it.cancel() }
        }
    }

    private suspend fun callApiWithParam(
        paramName: String,
        keyword: String,
        serviceKey: String,
        apiUrl: String
    ): List<IngredientResponse> = externalCallSemaphore.withPermit {

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

        return@withPermit try {
            val response = webClient.get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .awaitBody<Map<String, Any>>()

            verifyFoodExistence(response, keyword)
        } catch (e: Exception) {
            throw BusinessException(IngredientCode.OPEN_API_INGREDIENT_ERROR_01, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun verifyFoodExistence(response: Map<String, Any>, keyword: String): List<IngredientResponse> {
        val responseMap = response[KEY_RESPONSE] as? Map<String, Any> ?: return emptyList()
        val header = responseMap[KEY_HEADER] as? Map<String, Any>
        val body = responseMap[KEY_BODY] as? Map<String, Any>

        val resultMsg = header?.get(KEY_RESULT_MSG) as? String
        if (resultMsg == API_NO_DATA_MSG) {
            log.debug("검색 결과 없음 - keyword: {}", keyword)
            return emptyList()
        }

        val items = body?.get(KEY_ITEMS) as? List<Any>
        if (items.isNullOrEmpty()) {
            log.debug("결과 아이템 비어있음 - keyword: {}", keyword)
            return emptyList()
        }

        log.info("OpenAPI 검색 성공 - keyword: {}", keyword)
        return listOf(IngredientResponse(null, null, name = keyword, Source.OPEN_API))
    }
}