package com.example.home_recipe.global.response.code

enum class UserCode(override val code: String, override val message: String) : BaseCode {
    SIGNUP_SUCCESS("SIGNUP_SUCCESS", "회원가입 성공"),
    SIGNUP_ERROR_001("SIGNUP_ERROR_001", "필수 필드 누락"),
    SIGNUP_ERROR_002("SIGNUP_ERROR_002", "이름이 너무 짧거나 김"),
    SIGNUP_ERROR_003("SIGNUP_ERROR_003", "비밀번호 정책 불일치"),
    SIGNUP_ERROR_004("SIGNUP_ERROR_004", "이메일 형식 오류"),
    SIGNUP_ERROR_005("SIGNUP_ERROR_005", "중복된 이메일"),
    SIGNUP_ERROR_006("SIGNUP_ERROR_006", "약관 동의 누락"),
    SIGNUP_ERROR_007("SIGNUP_ERROR_007", "중복된 로그인ID로 회원가입 시도"),
    SIGNUP_ERROR_008("SIGNUP_ERROR_008", "중복된 휴대폰번호로 회원가입 시도"),
    SIGNUP_ERROR_009("SIGNUP_ERROR_009", "로그인ID가 정책을 만족하지 않음"),
    SIGNUP_ERROR_010("SIGNUP_ERROR_010", "휴대폰번호 형식이 올바르지 않음"),
    SIGNUP_ERROR_011("SIGNUP_ERROR_011", "유효성 검사 오류"),

    LOGIN_SUCCESS("LOGIN_SUCCESS", "로그인 성공"),
    LOGIN_ERROR_001("LOGIN_ERROR_001", "필수 필드 누락"),
    LOGIN_ERROR_002("LOGIN_ERROR_002", "존재하지 않는 아이디"),
    LOGIN_ERROR_003("LOGIN_ERROR_003", "비밀번호 불일치"),

    AUTH_SUCCESS("AUTH_SUCCESS", "토큰 검증 성공"),
    AUTH_RENEWAL_SUCCESS("AUTH_RENEWAL_SUCCESS", "토큰 갱신 성공"),
    AUTH_ERROR_001("AUTH_ERROR_001", "Access Token 누락"),
    AUTH_ERROR_002("AUTH_ERROR_002", "Access Token 만료"),
    AUTH_ERROR_003("AUTH_ERROR_003", "Access Token 위조"),
    AUTH_ERROR_004("AUTH_ERROR_004", "Refresh Token 누락"),
    AUTH_ERROR_005("AUTH_ERROR_005", "Refresh Token 만료"),
    AUTH_ERROR_006("AUTH_ERROR_006", "Refresh Token 위조")
}