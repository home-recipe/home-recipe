package com.example.home_recipe.global.response.code

enum class AuthCode (
    override val code: String,
    override val message: String
) : BaseCode {

    AUTH_INVALID_TOKEN("AUTH_001", "토큰이 유효하지 않습니다."),
    AUTH_EXPIRED_TOKEN("AUTH_002", "토큰이 만료되었습니다."),
    AUTH_NOT_EXIST_TOKEN("AUTH_003", "인증정보가 없습니다."),
    AUTH_INVALID_CREDENTIALS("AUTH_004", "이메일 또는 비밀번호가 올바르지 않습니다"),
    AUTH_FORBIDDEN("AUTH_005", "제한된 접근입니다."),

    AUTH_REFRESH_EXPIRED_TOKEN("AUTH_006", "Refresh Token이 만료되었습니다."),
    AUTH_REFRESH_INVALID_TOKEN("AUTH_007", "Refresh TOken이 유효하지 않습니다."),

    AUTH_OAUTH2_LOGIN_FAILED("AUTH_008", "OAuth2 로그인에 실패했습니다."),
    AUTH_OAUTH2_INVALID_USER_INFO("AUTH_009", "OAuth2 사용자 정보를 가져올 수 없습니다."),

    AUTH_LOGIN_SUCCESS("AUTH_LOGIN_SUCCESS", "로그인 성공"),
    AUTH_LOGOUT_SUCCESS("AUTH_LOGOUT_SUCCESS", "로그아웃 성공"),
    AUTH_REISSUE_SUCCESS("AUTH_REISSUE_SUCCESS", "토큰 재발급 성공"),

    MANAGE_SUCCESS_001("MANAGE_SUCCESS_001", "모든 사용자 조회 성공"),
    MANAGE_SUCCESS_002("MANAGE_SUCCESS_002", "사용자 ROLE UPDATE 성공")
}