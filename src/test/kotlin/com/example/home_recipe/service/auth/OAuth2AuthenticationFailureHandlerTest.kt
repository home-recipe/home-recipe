package com.example.home_recipe.service.auth

import com.example.home_recipe.global.response.code.AuthCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.context.ActiveProfiles

class OAuth2AuthenticationFailureHandlerTest {
    private val handler = OAuth2AuthenticationFailureHandler()
    private val om = ObjectMapper()

    @Test
    fun `OAuth2_인증_실패_시_401과_AUTH_008_응답을_반환한다`() {
        // given
        val request: HttpServletRequest = mock()
        val response = MockHttpServletResponse()
        val exception = BadCredentialsException("ignored")

        // when
        handler.onAuthenticationFailure(request, response, exception)

        // then
        assertEquals(401, response.status)
        assertEquals("application/json;charset=UTF-8", response.contentType)

        val actualBody = response.contentAsString

        assertTrue(actualBody.isNotBlank()) { "응답 바디가 비어 있습니다." }

        val actualCode = om.readTree(actualBody)
                .path("response")
                .path("code")
                .asText()

        assertEquals(AuthCode.AUTH_OAUTH2_LOGIN_FAILED.code, actualCode)
    }
}
