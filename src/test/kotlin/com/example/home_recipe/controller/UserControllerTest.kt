package com.example.home_recipe.controller

import com.example.home_recipe.controller.dto.auth.dto.TokenDto
import com.example.home_recipe.controller.dto.user.UserController
import com.example.home_recipe.controller.dto.user.dto.JoinRequest
import com.example.home_recipe.controller.dto.user.dto.JoinResponse
import com.example.home_recipe.controller.dto.user.dto.LoginRequest
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.global.sercurity.JwtProvider
import com.example.home_recipe.service.auth.AuthService
import com.example.home_recipe.service.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
@ExtendWith(MockitoExtension::class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var jwtProvider: JwtProvider

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

    fun getLoginRequest(): LoginRequest {
        return LoginRequest(
            email = "user123@naver.com",
            password = "password123"
        )
    }

    fun getTokenDto(): TokenDto {
        return TokenDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )
    }

    //////해피 테스트
    @Test
    @DisplayName("회원가입 성공시 201 Created 응답을 반환한다.")
    fun 회원가입_성공_테스트() {
        //given
        val request = getJoinReqeust()
        val response = getJoinResponse(request)

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_SUCCESS"))
            .andExpect(jsonPath("$.response.data.email").value("user123@naver.com"))
            .andDo(print())
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    fun 로그인_성공_테스트() {
        //given
        val request = getLoginRequest()
        val token = getTokenDto()

        whenever(authService.login(request)).thenReturn(token)

        //when & then
        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.code").value("LOGIN_SUCCESS"))
            .andExpect(jsonPath("$.response.data.accessToken").value("accessToken"))
            .andDo(print())
    }

    //////예외 테스트

    fun <T : Any> getTemplate(url: String, codeName: String, request: T) {
        mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value(codeName))
            .andDo(print())
    }


    @Test
    @DisplayName("이름이 정책을 만족하지 않을 경우 : SIGNUP_ERROR_002")
    fun 이름이_정책을_만족하지_않을_경우_SIGNUP_ERROR_002_반환() {
        //given
        val request = JoinRequest(
            name = "u",
            password = "password123",
            email = "user123@naver.com",
        )
        val response = getJoinResponse(request)

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        getTemplate("/user", "SIGNUP_ERROR_002", request)
    }

    @Test
    @DisplayName("비밀번호가 정책을 만족하지 않을 경우 : SIGNUP_ERROR_003")
    fun 비밀번호가_정책을_만족하지_않을_경우_SIGNUP_ERROR_003_반환() {
        //given
        val request = JoinRequest(
            name = "user",
            password = "passwo",
            email = "user123@naver.com",
        )
        val response = getJoinResponse(request)

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        getTemplate("/user", "SIGNUP_ERROR_003", request)
    }

    @Test
    @DisplayName("이메일이 정책을 만족하지 않을 경우 : SIGNUP_ERROR_004")
    fun 이메일이_정책을_만족하지_않을_경우_SIGNUP_ERROR_004_반환() {
        //given
        val request = JoinRequest(
            name = "user",
            password = "password123",
            email = "user123",
        )
        val response = getJoinResponse(request)

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        getTemplate("/user", "SIGNUP_ERROR_004", request)
    }

    private fun toJson(obj: Any): String =
        ObjectMapper().registerKotlinModule().writeValueAsString(obj)
}