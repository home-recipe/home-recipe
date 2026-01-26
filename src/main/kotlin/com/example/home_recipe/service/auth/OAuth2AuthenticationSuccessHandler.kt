package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.auth.dto.response.LoginResponse
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.domain.auth.oauth2.OAuth2Constants
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
) : AuthenticationSuccessHandler {
    private val objectMapper =
        ObjectMapper()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal: OAuth2User = authentication.principal as OAuth2User
        val email: String = principal.getAttribute(OAuth2Constants.EMAIL)
            ?: throw BusinessException(baseCode = AuthCode.AUTH_OAUTH2_INVALID_USER_INFO, status = HttpStatus.UNAUTHORIZED)

        val user = userRepository.findByEmail(email)
                .orElseThrow { BusinessException(baseCode = AuthCode.AUTH_OAUTH2_INVALID_USER_INFO, status = HttpStatus.UNAUTHORIZED) }

        val accessToken: String = jwtTokenProvider.createAccessToken(user.email, user.role)
        val refreshToken: String = jwtTokenProvider.createRefreshToken(user.email, user.role)

        tokenService.synchronizeRefreshToken(user, refreshToken)
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)

        response.status = HttpServletResponse.SC_OK
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = SecurityResponseConstants.CHARACTER_ENCODING_UTF_8

        val body = ApiResponse.success(
                data = LoginResponse(accessToken, refreshToken, user.role),
                responseCode = AuthCode.AUTH_LOGIN_SUCCESS,
                status = HttpStatus.OK).body

        response.writer.write(objectMapper.writeValueAsString(body))
    }
}