package com.example.home_recipe.global.response.code

enum class CommonCode(
    override val code: String,
    override val message: String
) : BaseCode {

    VALIDATION_ERROR("COMMON_001", "유효성 검사 실패"),
    UNKNOWN_ERROR("COMMON_999", "알 수 없는 오류 발생")
}