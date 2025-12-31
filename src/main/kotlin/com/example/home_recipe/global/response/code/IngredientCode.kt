package com.example.home_recipe.global.response.code

enum class IngredientCode(override val code: String, override val message: String) : BaseCode {
    CREATE_SUCCESS("INGREDIENT_CREATE_SUCCESS", "재료 추가 성공"),
    UPDATE_SUCCESS("INGREDIENT_UPDATE_SUCCESS", "재료 수정 성공"),
    FIND_SUCCESS("INGREDIENT_FIND_SUCCESS", "재료 조회하기 성공"),
    INGREDIENT_ERROR_005("INGREDIENT_ERROR_005", "재료명 누락"),
    INGREDIENT_ERROR_006("INGREDIENT_ERROR_006", "비로그인 요청"),
    INGREDIENT_ERROR_007("INGREDIENT_ERROR_007", "다른 사용자 냉장고 접근"),
    INGREDIENT_ERROR_008("INGREDIENT_ERROR_008", "잘못된 재료 ID"),
    INGREDIENT_ERROR_011("INGREDIENT_ERROR_011", "해당 재료 없음"),
    INGREDIENT_ERROR_012("INGREDIENT_ERROR_012", "재료 이름 제한 조건 불만족"),
    INGREDIENT_ERROR_013("INGREDIENT_ERROR_013", "재료 카테고리 누락")
}