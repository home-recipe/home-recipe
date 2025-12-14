package com.example.home_recipe.global.response.code

enum class UserCode(override val code: String, override val message: String) : BaseCode {
    SIGNUP_SUCCESS("SIGNUP_SUCCESS", "회원가입 성공"),
    EMAIL_VALIDATION_SUCCESS("EMAIL_VALIDATION_SUCCESS", "이메일 중복 여부 확인 성공"),
    SIGNUP_ERROR_001("SIGNUP_ERROR_001", "필수 필드 누락"),
    SIGNUP_ERROR_002("SIGNUP_ERROR_002", "이름이 너무 짧거나 김"),
    SIGNUP_ERROR_003("SIGNUP_ERROR_003", "비밀번호 정책 불일치"),
    SIGNUP_ERROR_004("SIGNUP_ERROR_004", "이메일 형식 오류"),
    SIGNUP_ERROR_005("SIGNUP_ERROR_005", "중복된 이메일"),
    SIGNUP_ERROR_006("SIGNUP_ERROR_006", "약관 동의 누락"),
    SIGNUP_ERROR_011("SIGNUP_ERROR_011", "유효성 검사 오류"),

    LOGIN_ERROR_001("LOGIN_ERROR_001", "필수 필드 누락"),
    LOGIN_ERROR_002("LOGIN_ERROR_002", "존재하지 않는 이메일"),
    LOGIN_ERROR_003("LOGIN_ERROR_003", "비밀번호 불일치"),
}