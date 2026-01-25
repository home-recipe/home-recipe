package com.example.home_recipe.service.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler : AuthenticationFailureHandler {

    private val om = ObjectMapper()

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.setHeader("Cache-Control", "no-store")

        response.writer.write(
            om.writeValueAsString(
                mapOf(
                    "code" to 401,
                    "message" to "OAuth2 login failed",
                    "error" to exception.javaClass.simpleName,
                    "detail" to (exception.message ?: "")
                )
            )
        )
    }
}