package com.example.home_recipe.controller

import com.example.home_recipe.controller.dto.auth.dto.TokenDto
import com.example.home_recipe.controller.dto.auth.AuthController
import com.example.home_recipe.global.sercurity.JwtProvider
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.auth.AuthService

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
@ExtendWith(MockitoExtension::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var authService: AuthService
    @MockBean
    private lateinit var userRepository: UserRepository
    @MockBean
    private lateinit var jwtProvider: JwtProvider
    @MockBean
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    @MockBean
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    @DisplayName("access/refresh Token 정상 재발급 테스트")
    fun access_refresh_token_정상_재발급_테스트() {
        //given
        val token = TokenDto(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )
        val newToken = TokenDto(
            accessToken = "newAccessToken",
            refreshToken = "newRefreshToken"
        )
        whenever(authService.refreshToken(any())).thenReturn(newToken)

        //when&then
        mockMvc.perform(
            post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(token))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.code").value("AUTH_RENEWAL_SUCCESS"))
            .andExpect(jsonPath("$.response.data.accessToken").value("newAccessToken"))
    }

    private fun toJson(obj: Any): String =
        ObjectMapper().registerKotlinModule().writeValueAsString(obj)
}