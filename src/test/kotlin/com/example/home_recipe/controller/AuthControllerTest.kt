package com.example.home_recipe.controller

import com.example.home_recipe.controller.dto.auth.dto.response.LoginResponse
import com.example.home_recipe.controller.dto.user.dto.request.JoinRequest
import com.example.home_recipe.controller.dto.user.dto.request.LoginRequest
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.auth.AuthService
import com.example.home_recipe.service.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.transaction.Transactional
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
    }

    /////// 해피 테스트
    @Test
    @DisplayName("정상적으로 로그인될 시 토큰이 발급된다.")
    fun login() {
        //given
        saveUser()
        val request = LoginRequest(EMAIL, PASSWORD)

        //when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("리프레시 토큰을 통해 액세스 토큰을 발급받는다.")
    fun 리프레시_토큰을_통해_엑세스_토큰을_발급받는다() {
        //given
        saveUser()
        val accessToken = extractLoginResponse().accessToken

        //when&then
        mockMvc.perform(
            post("/api/auth/reissue")
                .header("Authorization", "Bearer " + accessToken)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("로그아웃을 한다.")
    fun 로그아웃을_한다() {
        //given
        saveUser()
        val accessToken = extractLoginResponse().accessToken

        //when & then
        mockMvc.perform(
            post("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
        )
            .andExpect(status().isOk)
    }

    /////// 예외 테스트
    @ParameterizedTest
    @DisplayName("잘못된 이메일 형식으로 로그인할 시 400코드가 반환된다.")
    @ValueSource(strings = ["email", "@email", "email!"])
    fun 잘못된_이메일_형식으로_로그인할_시_400_코드_반환(email: String) {
        //given
        val request = LoginRequest(email, PASSWORD)

        //when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_004"))
    }

    @ParameterizedTest
    @DisplayName("비어있는 이메일로 로그인할 시 400코드가 반환된다.")
    @ValueSource(strings = [""])
    fun 비어있는_이메일로_로그인할_시_400_코드_반환(email: String) {
        //given
        val request = LoginRequest(email, "password123")

        //when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("LOGIN_ERROR_001"))
    }

    @Test
    @DisplayName("리프레시 토큰 없이 재발급 요청을 하면 401코드가 반환된다.")
    fun 리프레시_토큰_없이_재발급_요청을_하면_에러가_발생한다() {
        //given

        //when&then
        mockMvc.perform(
            post("/api/auth/reissue")
                .header("Authorization", "Bearer " + "")
        )
            .andExpect(status().isUnauthorized)
    }

    private fun toJson(obj: Any): String =
        ObjectMapper().registerKotlinModule().writeValueAsString(obj)

    private fun saveUser() {
        val request = JoinRequest(NAME, PASSWORD, EMAIL)
        userService.join(request)
    }

    private fun extractLoginResponse(): LoginResponse {
        val loginRequest = LoginRequest(EMAIL, PASSWORD)
        return authService.login(loginRequest)
    }
}