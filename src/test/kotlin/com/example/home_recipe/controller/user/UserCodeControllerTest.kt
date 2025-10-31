package com.example.home_recipe.controller.user

import com.example.home_recipe.controller.auth.dto.TokenDto
import com.example.home_recipe.controller.user.dto.JoinRequest
import com.example.home_recipe.controller.user.dto.JoinResponse
import com.example.home_recipe.controller.user.dto.LoginRequest
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.global.sercurity.JwtProvider
import com.example.home_recipe.service.auth.AuthService
import com.example.home_recipe.service.auth.AuthServiceTest
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

    //////해피 테스트
    @Test
    @DisplayName("회원가입 성공시 201 Created 응답을 반환한다.")
    fun 회원가입_성공_테스트() {
        //given
        val request = JoinRequest(
            name = "userName",
            loginId = "user123",
            password = "password123",
            email = "user123@naver.com",
            phoneNumber = "01011112222"
        )
        val response = JoinResponse(
            name = request.name,
            loginId = request.loginId,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = Role.USER.name
        )

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_SUCCESS"))
            .andExpect(jsonPath("$.response.data.loginId").value("user123"))
            .andDo(print())
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    fun 로그인_성공_테스트() {
        //given
        val request = LoginRequest(
            loginId = "user123",
            password = "password123"
        )
        val token = TokenDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

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
    @Test
    @DisplayName("이름이 정책을 만족하지 않을 경우 : SIGNUP_ERROR_002")
    fun 이름이_정책을_만족하지_않을_경우_SIGNUP_ERROR_002_반환() {
        //given
        val request = JoinRequest(
            name = "u",
            loginId = "user123",
            password = "password123",
            email = "user123@naver.com",
            phoneNumber = "01011112222"
        )
        val response = JoinResponse(
            name = request.name,
            loginId = request.loginId,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = Role.USER.name
        )

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_002"))
            .andDo(print())
    }

    @Test
    @DisplayName("비밀번호가 정책을 만족하지 않을 경우 : SIGNUP_ERROR_003")
    fun 비밀번호가_정책을_만족하지_않을_경우_SIGNUP_ERROR_003_반환() {
        //given
        val request = JoinRequest(
            name = "user",
            loginId = "user123",
            password = "p",
            email = "user123@naver.com",
            phoneNumber = "01011112222"
        )
        val response = JoinResponse(
            name = request.name,
            loginId = request.loginId,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = Role.USER.name
        )

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_003"))
            .andDo(print())
    }

    @Test
    @DisplayName("이메일이 정책을 만족하지 않을 경우 : SIGNUP_ERROR_004")
    fun 이메일이_정책을_만족하지_않을_경우_SIGNUP_ERROR_004_반환() {
        //given
        val request = JoinRequest(
            name = "user",
            loginId = "user123",
            password = "password123",
            email = "user123",
            phoneNumber = "01011112222"
        )
        val response = JoinResponse(
            name = request.name,
            loginId = request.loginId,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = Role.USER.name
        )

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_004"))
            .andDo(print())
    }

    @Test
    @DisplayName("로그인ID가 정책을 만족하지 않을 경우 : SIGNUP_ERROR_009")
    fun 로그인ID가_정책을_만족하지_않을_경우_SIGNUP_ERROR_009_반환() {
        //given
        val request = JoinRequest(
            name = "user",
            loginId = "u",
            password = "password123",
            email = "user123@naver.com",
            phoneNumber = "01011112222"
        )
        val response = JoinResponse(
            name = request.name,
            loginId = request.loginId,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = Role.USER.name
        )

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_009"))
            .andDo(print())
    }

    @Test
    @DisplayName("휴대폰번호가 정책을 만족하지 않을 경우 : SIGNUP_ERROR_010")
    fun 휴대폰번호가_정책을_만족하지_않을_경우_SIGNUP_ERROR_010_반환() {
        //given
        val request = JoinRequest(
            name = "user",
            loginId = "user123",
            password = "password123",
            email = "user123@naver.com",
            phoneNumber = "0101111"
        )
        val response = JoinResponse(
            name = request.name,
            loginId = request.loginId,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = Role.USER.name
        )

        whenever(userService.join(request)).thenReturn(response)

        //when & then
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("SIGNUP_ERROR_010"))
            .andDo(print())
    }

    @Test
    @DisplayName("DTO loginId 누락인 경우 LOGIN_ERROR_001 반환")
    fun DTO_필드_누락인_경우_LOGIN_ERROR_001_반환() {
        //given
        val request = LoginRequest(
            loginId = "",
            password = "password123"
        )
        val token = TokenDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        whenever(authService.login(request)).thenReturn(token)

        //when & then
        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("LOGIN_ERROR_001"))

    }

    @Test
    @DisplayName("DTO password 누락인 경우 LOGIN_ERROR_003 반환")
    fun DTO_password_필드_누락인_경우_LOGIN_ERROR_003_반환() {
        //given
        val request = LoginRequest(
            loginId = "user123",
            password = ""
        )
        val token = TokenDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        whenever(authService.login(request)).thenReturn(token)

        //when & then
        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("LOGIN_ERROR_003"))

    }

    @Test
    @DisplayName("loginId 정보가 존재하지 않는 경우 LOGIN_ERROR_002 반환")
    fun loginId_정보가_존재하지_않는_경우_LOGIN_ERROR_002_반환() {
        //given
        val request = LoginRequest(
            loginId = "user123",
            password = "password123"
        )

        whenever(authService.login(request))
            .thenThrow(BusinessException(ResponseCode.LOGIN_ERROR_002, HttpStatus.BAD_REQUEST))

        //when & then
        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.response.code").value("LOGIN_ERROR_002"))

    }


    private fun toJson(obj: Any): String =
        ObjectMapper().registerKotlinModule().writeValueAsString(obj)
}