package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.auth.dto.response.LoginResponse
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
) : AuthenticationSuccessHandler {

    private val objectMapper = ObjectMapper()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal as org.springframework.security.oauth2.core.user.OAuth2User
        val email = principal.getAttribute<String>("localEmail")
            ?: throw IllegalStateException("Missing localEmail in principal")

        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalStateException("User not found after oauth2 login: $email") }

        val accessToken = jwtTokenProvider.createAccessToken(user.email, user.role)
        val refreshToken = jwtTokenProvider.createRefreshToken(user.email, user.role)

        tokenService.synchronizeRefreshToken(user, refreshToken)

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)

        response.status = HttpServletResponse.SC_OK
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                LoginResponse(accessToken, refreshToken, user.role)
            )
        )
    }
}