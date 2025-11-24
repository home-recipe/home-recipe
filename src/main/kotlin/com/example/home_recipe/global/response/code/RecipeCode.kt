package com.example.home_recipe.global.response.code

enum class RecipeCode(override val code: String, override val message: String) : BaseCode {
    RECIPE_SUCCESS("RECIPE_SUCCESS", "레시피 생성 성공"),
    RECIPE_ERROR_001("RECIPE_ERROR_001", "요청 형식 오류"),
    RECIPE_ERROR_002("RECIPE_ERROR_002", "비로그인 요청"),
    RECIPE_ERROR_003("RECIPE_ERROR_003", "다른 사용자 냉장고 접근"),
    RECIPE_ERROR_004("RECIPE_ERROR_004", "냉장고 정보 없음")
}