package com.example.home_recipe.global.response.code

enum class RefrigeratorCode(override val code: String, override val message: String) : BaseCode {
    REFRIGERATOR_SUCCESS("REFRIGERATOR_SUCCESS", "냉장고 조회 성공"),
    REFRIGERATOR_ERROR_001("REFRIGERATOR_ERROR_001", "요청 파라미터 오류"),
    REFRIGERATOR_ERROR_002("REFRIGERATOR_ERROR_002", "로그인하지 않음"),
    REFRIGERATOR_ERROR_003("REFRIGERATOR_ERROR_003", "다른 사용자의 냉장고 접근"),
    REFRIGERATOR_ERROR_004("REFRIGERATOR_ERROR_004", "냉장고 없음"),
    REFRIGERATOR_ERROR_005("REFRIGERATOR_ERROR_005", "이미 냉장고에 있는 재료입니다"),

    CREATE_SUCCESS("REFRIGERATOR_CREATE_SUCCESS", "냉장고 생성 성공"),

    ADD_INGREDIENT_SUCCESS("REFRIGERATOR_ADD_INGREDIENT_SUCCESS", "냉장고 재료 추가 성공"),

    USE_INGREDIENT_SUCCESS("REFRIGERATOR_USE_INGREDIENT_SUCCESS", "냉장고 재료 사용 성공")
}