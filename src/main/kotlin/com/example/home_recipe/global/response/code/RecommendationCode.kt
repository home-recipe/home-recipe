package com.example.home_recipe.global.response.code

enum class RecommendationCode(override val code: String, override val message: String) : BaseCode {
    RECOMMENDATION_SUCCESS("RECOMMENDATION_SUCCESS", "추천 성공"),
    RECOMMENDATION_ERROR_001("RECOMMENDATION_ERROR_001", "요청 형식 오류"),
    RECOMMENDATION_ERROR_002("RECOMMENDATION_ERROR_002", "비로그인 요청"),
    RECOMMENDATION_ERROR_003("RECOMMENDATION_ERROR_003", "다른 사용자 냉장고 접근"),
    RECOMMENDATION_ERROR_004("RECOMMENDATION_ERROR_004", "냉장고 정보 없음")
}