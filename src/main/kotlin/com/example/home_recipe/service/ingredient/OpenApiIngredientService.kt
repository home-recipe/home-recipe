package com.example.home_recipe.service.ingredient

import com.example.home_recipe.controller.ingredient.dto.response.FoodItemDto
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI
import java.net.URLEncoder

@Service
class OpenApiIngredientService(
    @Value("\${external-api.raw-food.service-key}") private val rawFoodKey: String,
    @Value("\${external-api.process-food.service-key}") private val processFoodKey: String,
    @Value("\${external-api.raw-food.url}") private val apiUrl: String,
    private val webClientBuilder: WebClient.Builder
) {
    companion object {
        private val FOOD_SEARCH_LEVELS = listOf("foodNm", "foodLv4Nm", "foodLv5Nm", "foodLv6Nm")

        private const val RESPONSE_TYPE = "json"
        private const val DEFAULT_PAGE_NO = 1
        private const val DEFAULT_NUM_OF_ROWS = 5
        private const val ENCODING_TYPE = "UTF-8"
        private const val HEADER_NAME = "Accept"
        private const val HEADER_VALUE = "application/json"

        private const val API_NO_DATA_MSG = "NODATA_ERROR"

        private const val KEY_RESPONSE = "response"
        private const val KEY_HEADER = "header"
        private const val KEY_BODY = "body"
        private const val KEY_ITEMS = "items"
        private const val KEY_RESULT_MSG = "resultMsg"
    }

    suspend fun searchExternalFood(keyword: String): List<FoodItemDto> {
        for (level in FOOD_SEARCH_LEVELS) {
            val result = callApiWithParam(level, keyword, rawFoodKey)
            if (result.isNotEmpty()) return result
        }
        for (level in FOOD_SEARCH_LEVELS) {
            val result = callApiWithParam(level, keyword, processFoodKey)
            if (result.isNotEmpty()) return result
        }
        return emptyList()
    }

    suspend fun callApiWithParam(paramName: String, keyword: String, serviceKey: String): List<FoodItemDto> {
        val encodedKeyword = URLEncoder.encode(keyword, ENCODING_TYPE)

        val finalUrl = "${apiUrl}?serviceKey=$serviceKey" +
                "&type=$RESPONSE_TYPE" +
                "&$paramName=$encodedKeyword" +
                "&pageNo=$DEFAULT_PAGE_NO" +
                "&numOfRows=$DEFAULT_NUM_OF_ROWS"

        return try {
            val response = webClientBuilder.build().get()
                .uri(URI(finalUrl))
                .header(HEADER_NAME, HEADER_VALUE)
                .retrieve()
                .awaitBody<Map<String, Any>>()
            verifyFoodExistence(response, keyword)
        } catch (e: Exception) {
            throw BusinessException(IngredientCode.OPEN_API_INGREDIENT_ERROR_01, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun verifyFoodExistence(response: Map<String, Any>, keyword: String): List<FoodItemDto> {
        val responseMap = response[KEY_RESPONSE] as? Map<String, Any> ?: return emptyList()
        val header = responseMap[KEY_HEADER] as? Map<String, Any>
        val body = responseMap[KEY_BODY] as? Map<String, Any>

        val resultMsg = header?.get(KEY_RESULT_MSG) as? String
        if (resultMsg == API_NO_DATA_MSG) {
            println("검색 결과 없음: $keyword")
            return emptyList()
        }

        val items = body?.get(KEY_ITEMS) as? List<Any>
        if (items.isNullOrEmpty()) {
            println("결과 아이템이 비어있음: $keyword")
            return emptyList()
        }

        println("--- 검증 성공: '$keyword'를 리스트에 담습니다 ---")
        return listOf(FoodItemDto(name = keyword))
    }
}