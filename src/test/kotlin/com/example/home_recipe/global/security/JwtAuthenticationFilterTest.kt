package com.example.home_recipe.global.security

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ResponseCode
import com.example.home_recipe.global.sercurity.JwtAuthenticationFilter
import com.example.home_recipe.global.sercurity.JwtProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockitoExtension::class)
class JwtAuthenticationFilterTest {
    @Mock
    private lateinit var jwtProvider: JwtProvider

    @InjectMocks
    private lateinit var filter: JwtAuthenticationFilter


    //////해피 테스트
    @Test
    @DisplayName("유효한 토큰이 있을 때 SecurityContext에 인증 정보가 저장된다.")
    fun 유효한_토큰이_있을_때_SecurityContext에_인증_정보_저장() {
        //given
        val token = "valid_token"
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        request.addHeader("Authorization", "Bearer $token")

        whenever(jwtProvider.validateAccessToken(any())).thenReturn(ResponseCode.AUTH_SUCCESS)
        whenever(jwtProvider.getEmailFromToken(any())).thenReturn("user123@naver.com")

        //when
        filter.doFilter(request, response, filterChain)

        //then
        val auth = SecurityContextHolder.getContext().authentication
        assertThat(auth).isNotNull
        assertThat(auth.principal).isEqualTo("user123@naver.com")
    }

    ///////예외 테스트
    @Test
    @DisplayName("만료된 토큰이 있을 때 Code : AUTH_ERROR_002 예외가 발생한다.")
    fun 만료된_토큰이_있을_때_AUTH_ERROR_002_예외_발생() {
        //given
        val token = "invalid_token"
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        request.addHeader("Authorization", "Bearer $token")

        whenever(jwtProvider.validateAccessToken(any()))
            .thenThrow(BusinessException(ResponseCode.AUTH_ERROR_002, HttpStatus.UNAUTHORIZED))

        //when
        val exception = assertThrows<BusinessException> {
            filter.doFilter(request, response, filterChain)
        }

        //then
        assertThat(exception.responseCode).isEqualTo(ResponseCode.AUTH_ERROR_002)
        assertThat(exception.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    @DisplayName("위조된 토큰이 있을 때 Code : AUTH_ERROR_003 예외가 발생한다.")
    fun 위조된_토큰이_있을_때_AUTH_ERROR_003_예외_발생() {
        //given
        val token = "invalid_token"
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()
        request.addHeader("Authorization", "Bearer $token")

        whenever(jwtProvider.validateAccessToken(any()))
            .thenThrow(BusinessException(ResponseCode.AUTH_ERROR_003, HttpStatus.UNAUTHORIZED))

        //when
        val exception = assertThrows<BusinessException> {
            filter.doFilter(request, response, filterChain)
        }

        //then
        assertThat(exception.responseCode).isEqualTo(ResponseCode.AUTH_ERROR_003)
        assertThat(exception.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}