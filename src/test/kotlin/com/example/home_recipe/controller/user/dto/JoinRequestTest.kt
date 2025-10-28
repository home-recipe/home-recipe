package com.example.home_recipe.controller.user.dto

import com.example.home_recipe.global.response.ResponseCode
import jakarta.validation.Validation
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class JoinRequestTest {

    //////성공 테스트
    @Test
    @DisplayName("JoinRequest DTO 생성 성공 테스트")
    fun JoinRequest_DTO_생성_성공_테스트() {
        //given
        val request = JoinRequest(
            name = "userName",
            loginId = "test123",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)

        //then
        Assertions.assertThat(violations).isEmpty()
    }

    ///////예외 테스트
    @Test
    @DisplayName("이름이 공백일 경우 검증 실패")
    fun 이름이_공백일_경우_검증_실패() {
        //given
        val request = JoinRequest(
            name = "",
            loginId = "test123",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        Assertions.assertThat(violations).isNotEmpty
        Assertions.assertThat(message).isEqualTo(ResponseCode.SIGNUP_ERROR_001.name)
    }

    @ParameterizedTest
    @DisplayName("이름이 유효성 조건을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["김", "이름이너무너무너무길어요"])
    fun 이름이_유효성_조건_만족하지_않으면_검증_실패(name: String) {
        //given
        val request = JoinRequest(
            name = name,
            loginId = "test123",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        Assertions.assertThat(violations).isNotEmpty
        Assertions.assertThat(message).isEqualTo(ResponseCode.SIGNUP_ERROR_002.name)
    }

    @ParameterizedTest
    @DisplayName("아이디가 형식을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["tes1", "notExistNums", "010101"])
    fun 아이디가_형식을_만족하지_않을_경우_검증_실패(loginId: String) {
        //given
        val request = JoinRequest(
            name = "name",
            loginId = loginId,
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        Assertions.assertThat(violations).isNotEmpty
        Assertions.assertThat(message).isEqualTo(ResponseCode.SIGNUP_ERROR_009.name)
    }

    @ParameterizedTest
    @DisplayName("비밀번호가 형식을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["123", "thisPasswordIsTooLong12345"])
    fun 비밀번호가_형식을_만족하지_않을_경우_검증_실패(password: String) {
        //given
        val request = JoinRequest(
            name = "name",
            loginId = "test123",
            password = password,
            email = "test@example.com",
            phoneNumber = "01012345678"
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        Assertions.assertThat(violations).isNotEmpty
        Assertions.assertThat(message).isEqualTo(ResponseCode.SIGNUP_ERROR_003.name)
    }

    @ParameterizedTest
    @DisplayName("이메일이 형식을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["test123", "@missingLocalPart.com", "username@com.", "username@.com", "username@ domain.com"])
    fun 이메일이_형식을_만족하지_않을_경우_검증_실패(email: String) {
        //given
        val request = JoinRequest(
            name = "name",
            loginId = "test123",
            password = "password",
            email = email,
            phoneNumber = "01012345678"
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        Assertions.assertThat(violations).isNotEmpty
        Assertions.assertThat(message).isEqualTo(ResponseCode.SIGNUP_ERROR_004.name)
    }

    @ParameterizedTest
    @DisplayName("휴대폰번호가 형식을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["12341111", "0112345677", "0112345"])
    fun 휴대폰번호가_형식을_만족하지_않을_경우_검증_실패(phoneNumber: String) {
        //given
        val request = JoinRequest(
            name = "name",
            loginId = "test123",
            password = "password",
            email = "username@naver.com",
            phoneNumber = phoneNumber
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        Assertions.assertThat(violations).isNotEmpty
        Assertions.assertThat(message).isEqualTo(ResponseCode.SIGNUP_ERROR_010.name)
    }
}