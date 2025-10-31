package com.example.home_recipe.global.response.code

enum class IngredientCode(override val code: String, override val message: String) : BaseCode {
    INGREDIENT_SUCCESS("INGREDIENT_SUCCESS", "재료 추가 성공"),
    INGREDIENT_ERROR_005("INGREDIENT_ERROR_005", "재료명 누락"),
    INGREDIENT_ERROR_006("INGREDIENT_ERROR_006", "비로그인 요청"),
    INGREDIENT_ERROR_007("INGREDIENT_ERROR_007", "다른 사용자 냉장고 접근"),
    INGREDIENT_ERROR_008("INGREDIENT_ERROR_008", "잘못된 재료 ID"),
    INGREDIENT_ERROR_011("INGREDIENT_ERROR_011", "해당 재료 없음")
}