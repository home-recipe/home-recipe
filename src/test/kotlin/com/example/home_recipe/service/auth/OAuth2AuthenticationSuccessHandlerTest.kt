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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*

class OAuth2AuthenticationSuccessHandlerTest {
    private val userRepository: UserRepository = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val tokenService: TokenService = mock()
    private val authHelper: AuthHelper = mock()
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository = mock()
    private val handler = OAuth2AuthenticationSuccessHandler(
            userRepository = userRepository,
            jwtTokenProvider = jwtTokenProvider,
            tokenService = tokenService,
            authHelper = authHelper,
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
    @DisplayName("정상 로그인 시 토큰을 생성하고 authHelper를 통해 응답을 빌드한다")
    fun `Success_Login_Calls_AuthHelper`() {
        // given
        val request: HttpServletRequest = mock()
        val response = MockHttpServletResponse()
        val principal: OAuth2User = mock()
        val email = "test@example.com"
        val role = Role.USER
        val accessToken = "access-token"
        val refreshToken = "refresh-token"

        val user = mock<User>()
        whenever(user.email).thenReturn(email)
        whenever(user.role).thenReturn(role)

        val authentication: Authentication = mock()
        whenever(authentication.principal).thenReturn(principal)
        whenever(principal.getAttribute<String>(OAuth2Constants.EMAIL)).thenReturn(email)
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        whenever(jwtTokenProvider.createAccessToken(email, role)).thenReturn(accessToken)
        whenever(jwtTokenProvider.createRefreshToken(email, role)).thenReturn(refreshToken)

        // when
        handler.onAuthenticationSuccess(request, response, authentication)

        // then
        verify(tokenService).synchronizeRefreshToken(user, refreshToken)
        verify(authorizationRequestRepository).removeAuthorizationRequestCookies(request, response)
        verify(authHelper).buildResponse(accessToken, refreshToken, request, response)
    }

    private fun responseCode(body: String): String = om.readTree(body)
        .path("response")
        .path("code")
        .asText()
}