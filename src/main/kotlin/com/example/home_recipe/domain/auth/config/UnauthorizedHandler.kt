package com.example.home_recipe.domain.auth.config

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.ResponseDetail
import com.example.home_recipe.global.response.code.AuthCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class UnauthorizedHandler : AuthenticationEntryPoint {

    companion object {
        const val CONTENT_TYPE = "application/json;charset=UTF-8"
    }

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val authCode = when {
            authException is InvalidBearerTokenException &&
                    authException.message?.contains("expired", ignoreCase = true) == true ->
                AuthCode.AUTH_EXPIRED_TOKEN

            authException is InsufficientAuthenticationException ->
                AuthCode.AUTH_NOT_EXIST_TOKEN

            else ->
                AuthCode.AUTH_INVALID_TOKEN
        }

        val body = ApiResponse(
            code = HttpStatus.UNAUTHORIZED.value(),
            message = authCode.message,
            response = ResponseDetail(
                code = authCode.code,
                data = null
            )
        )

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = CONTENT_TYPE
        response.writer.write(
            ObjectMapper().writeValueAsString(body)
        )
    }
}