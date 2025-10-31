package com.example.home_recipe.global.response.code

enum class RefrigeratorCode(override val code: String, override val message: String) : BaseCode {
    REFRIGERATOR_SUCCESS("REFRIGERATOR_SUCCESS", "냉장고 조회 성공"),
    REFRIGERATOR_ERROR_001("REFRIGERATOR_ERROR_001", "요청 파라미터 오류"),
    REFRIGERATOR_ERROR_002("REFRIGERATOR_ERROR_002", "로그인하지 않음"),
    REFRIGERATOR_ERROR_003("REFRIGERATOR_ERROR_003", "다른 사용자의 냉장고 접근"),
    REFRIGERATOR_ERROR_004("REFRIGERATOR_ERROR_004", "냉장고 없음")
}