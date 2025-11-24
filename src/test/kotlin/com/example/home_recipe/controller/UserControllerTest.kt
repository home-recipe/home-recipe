package com.example.home_recipe.controller

import com.example.home_recipe.controller.dto.user.dto.request.JoinRequest
import com.example.home_recipe.service.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.transaction.Transactional
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var userService: UserService

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
    }


    //////해피 테스트
    @Test
    @DisplayName("회원가입 성공시 201 Created 응답을 반환한다.")
    fun join() {
        //given
        val request = JoinRequest(NAME, PASSWORD, EMAIL)

        //when & then
        mockMvc.perform(
            post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_SUCCESS"))
            .andExpect(jsonPath("$.response.data.email").value(EMAIL))
    }

    //////예외 테스트
    @ParameterizedTest
    @DisplayName("길이 정책을 만족하지 않는 이름값으로 회원가입 시 400코드가 반환된다.")
    @ValueSource(strings = ["longlonglongname", "l"])
    fun 길이_정책을_만족하지_않는_경우_400_코드_반환(name: String) {
        //given
        val request = JoinRequest(name, PASSWORD, EMAIL)

        //when & then
        mockMvc.perform(
            post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_002"))
    }

    @Test
    @DisplayName("공백인 이름값으로 회원가입 시 400코드가 반환된다.")
    fun 공백인_이름값으로_회원가입_시_400_코드_반환() {
        //given
        val request = JoinRequest("", PASSWORD, EMAIL)

        //when & then
        mockMvc.perform(
            post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
    }

    @ParameterizedTest
    @DisplayName("길이 정책을 만족하지 않는 패스워드 값으로 회원가입 시 400코드가 반환된다.")
    @ValueSource(strings = ["longlonglonglongpassword", "l", ""])
    fun 길이_정책을_만족하지_않는_패스워드_값으로_회원가입_시_400코드_반환(password: String) {
        //given
        val request = JoinRequest(NAME, password, EMAIL)

        //when & then
        mockMvc.perform(
            post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
    }

    @ParameterizedTest
    @DisplayName("이메일이 정책을 만족하지 않을 경우 : SIGNUP_ERROR_004")
    @ValueSource(strings = ["email", "@email", "email!", ""])
    fun 이메일이_정책을_만족하지_않을_경우_SIGNUP_ERROR_004_반환(email: String) {
        //given
        val request = JoinRequest(NAME, PASSWORD, email)

        //when & then
        mockMvc.perform(
            post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
    }

    private fun toJson(obj: Any): String =
        ObjectMapper().registerKotlinModule().writeValueAsString(obj)
}