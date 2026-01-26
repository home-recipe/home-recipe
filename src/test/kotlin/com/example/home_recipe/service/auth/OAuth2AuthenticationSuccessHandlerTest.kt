package com.example.home_recipe.service.auth

import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.domain.auth.oauth2.OAuth2Constants
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test-local")
class OAuth2AuthenticationSuccessHandlerTest {
    private val userRepository: UserRepository = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val tokenService: TokenService = mock()
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository = mock()
    private val handler = OAuth2AuthenticationSuccessHandler(
            userRepository = userRepository,
            jwtTokenProvider = jwtTokenProvider,
            tokenService = tokenService,
            authorizationRequestRepository = authorizationRequestRepository)

    private val om = ObjectMapper()

    @Test
    fun `Email이_없으면_401과_AUTH_009_응답을_반환한다`() {
        // given
        val request: HttpServletRequest = mock()
        val response = MockHttpServletResponse()
        val principal: OAuth2User = mock()

        whenever(principal.getAttribute<String>(OAuth2Constants.EMAIL)).thenReturn(null)
        val authentication: Authentication = mock()
        whenever(authentication.principal).thenReturn(principal)

        // when
        handler.onAuthenticationSuccess(request, response, authentication)

        // then
        assertEquals(401, response.status)
        assertEquals("application/json;charset=UTF-8", response.contentType)
        assertEquals(SecurityResponseConstants.CHARACTER_ENCODING_UTF_8, response.characterEncoding)

        val actualBody = response.contentAsString
        assertTrue(actualBody.isNotBlank()) { "응답 바디가 비어 있습니다." }

        val actualCode = responseCode(actualBody)
        assertEquals(AuthCode.AUTH_OAUTH2_INVALID_USER_INFO.code, actualCode)
    }

    @Test
    fun `Email은_있지만_유저가_없으면_401과_AUTH_009_응답을_반환한다`() {
        // given
        val request: HttpServletRequest = mock()
        val response = MockHttpServletResponse()
        val principal: OAuth2User = mock()
        val email = "test@example.com"

        whenever(principal.getAttribute<String>(OAuth2Constants.EMAIL)).thenReturn(email)
        val authentication: Authentication = mock()

        whenever(authentication.principal).thenReturn(principal)
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.empty())

        // when
        handler.onAuthenticationSuccess(request, response, authentication)

        // then
        assertEquals(401, response.status)
        val actualBody = response.contentAsString
        assertTrue(actualBody.isNotBlank()) { "응답 바디가 비어 있습니다." }
        val actualCode = responseCode(actualBody)
        assertEquals(AuthCode.AUTH_OAUTH2_INVALID_USER_INFO.code, actualCode)
    }

    @Test
    fun `정상_로그인_시_200과_토큰_정보를_포함한_응답을_반환한다`() {
        // given
        val request: HttpServletRequest = mock()
        val response = MockHttpServletResponse()
        val principal: OAuth2User = mock()
        val email = "test@example.com"

        whenever(principal.getAttribute<String>(OAuth2Constants.EMAIL)).thenReturn(email)

        val authentication: Authentication = mock()
        whenever(authentication.principal).thenReturn(principal)

        val user = mock<User>()
        whenever(user.email).thenReturn(email)

        val role = mock<Role>()
        val accessToken = "access-token"
        val refreshToken = "refresh-token"

        whenever(role.name).thenReturn("USER")
        whenever(user.role).thenReturn(role)
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))
        whenever(jwtTokenProvider.createAccessToken(email, role)).thenReturn(accessToken)
        whenever(jwtTokenProvider.createRefreshToken(email, role)).thenReturn(refreshToken)
        doNothing().whenever(tokenService).synchronizeRefreshToken(user, refreshToken)
        doNothing().whenever(authorizationRequestRepository).removeAuthorizationRequestCookies(any(), any())

        // when
        handler.onAuthenticationSuccess(request, response, authentication)

        // then
        assertEquals(200, response.status)
        assertEquals("application/json;charset=UTF-8", response.contentType)
        assertEquals(SecurityResponseConstants.CHARACTER_ENCODING_UTF_8, response.characterEncoding)

        val actualBody = response.contentAsString
        assertTrue(actualBody.isNotBlank()) { "응답 바디가 비어 있습니다." }

        val actualCode = responseCode(actualBody)
        assertEquals(AuthCode.AUTH_LOGIN_SUCCESS.code, actualCode)

        val dataNode = om.readTree(actualBody)
                .path("response")
                .path("data")

        assertEquals(accessToken, dataNode.path("accessToken").asText())
        assertEquals(refreshToken, dataNode.path("refreshToken").asText())
        assertEquals("USER", dataNode.path("role").asText())
    }

    private fun responseCode(body: String): String = om.readTree(body)
            .path("response")
            .path("code")
            .asText()
}
