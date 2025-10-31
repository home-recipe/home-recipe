package com.example.home_recipe.controller.dto.user

import com.example.home_recipe.controller.dto.user.dto.JoinRequest
import com.example.home_recipe.controller.dto.user.dto.JoinResponse
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.global.response.code.UserCode
import jakarta.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class JoinRequestTest {

    fun getJoinReqeust(): JoinRequest {
        return JoinRequest(
            name = "user",
            password = "password123",
            email = "user123@naver.com",
        )
    }

    fun getJoinResponse(request: JoinRequest): JoinResponse {
        return JoinResponse(
            name = request.name,
            email = request.email,
            role = Role.USER.name
        )
    }

    //////성공 테스트
    @Test
    @DisplayName("JoinRequest DTO 생성 성공 테스트")
    fun JoinRequest_DTO_생성_성공_테스트() {
        //given
        val request = getJoinReqeust()

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)

        //then
        assertThat(violations).isEmpty()
    }

    ///////예외 테스트
    @Test
    @DisplayName("이름이 공백일 경우 검증 실패")
    fun 이름이_공백일_경우_검증_실패() {
        //given
        val request = JoinRequest(
            name = "",
            password = "password123",
            email = "user123@naver.com",
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        assertThat(violations).isNotEmpty
        assertThat(message).isEqualTo(UserCode.SIGNUP_ERROR_002.name)
    }

    @ParameterizedTest
    @DisplayName("이름이 유효성 조건을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["김", "이름이너무너무너무길어요"])
    fun 이름이_유효성_조건_만족하지_않으면_검증_실패(name: String) {
        //given
        val request = JoinRequest(
            name = name,
            password = "password123",
            email = "test@example.com",
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        assertThat(violations).isNotEmpty
        assertThat(message).isEqualTo(UserCode.SIGNUP_ERROR_002.name)
    }

    @ParameterizedTest
    @DisplayName("비밀번호가 형식을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["123", "thisPasswordIsTooLong12345"])
    fun 비밀번호가_형식을_만족하지_않을_경우_검증_실패(password: String) {
        //given
        val request = JoinRequest(
            name = "name",
            password = password,
            email = "test@example.com",
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        assertThat(violations).isNotEmpty
        assertThat(message).isEqualTo(UserCode.SIGNUP_ERROR_003.name)
    }

    @ParameterizedTest
    @DisplayName("이메일이 형식을 만족하지 않을 경우 검증 실패")
    @ValueSource(strings = ["test123", "@missingLocalPart.com", "username@com.", "username@.com", "username@ domain.com"])
    fun 이메일이_형식을_만족하지_않을_경우_검증_실패(email: String) {
        //given
        val request = JoinRequest(
            name = "name",
            password = "password",
            email = email,
        )

        //when
        val validator = Validation.buildDefaultValidatorFactory().validator
        val violations = validator.validate(request)
        val message = violations.first().message

        //then
        assertThat(violations).isNotEmpty
        assertThat(message).isEqualTo(UserCode.SIGNUP_ERROR_004.name)
    }
}