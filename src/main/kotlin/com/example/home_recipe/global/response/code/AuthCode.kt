package com.example.home_recipe.global.response.code

enum class AuthCode (
    override val code: String,
    override val message: String
) : BaseCode {

    AUTH_INVALID_TOKEN("AUTH_001", "토큰이 유효하지 않습니다."),
    AUTH_REQUIRED_LOGIN("AUTH_002", "로그인이 필요합니다."),
    AUTH_EXPIRED_TOKEN("AUTH_003", "토큰의 유효기간이 만료되었습니다."),
    AUTH_FORBIDDEN("AUTH_004", "제한된 접근입니다."),

    AUTH_LOGIN_SUCCESS("LOGIN_SUCCESS", "로그인 성공"),
    AUTH_LOGOUT_SUCCESS("LOGOUT_SUCCESS", "로그아웃 성공"),
    AUTH_REISSUE_SUCCESS("AUTH_REISSUE_SUCCESS", "토큰 재발급 성공")
}