package com.example.home_recipe.domain.auth.config

import com.example.home_recipe.global.response.code.AuthCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
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
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = CONTENT_TYPE
        val json = """{"message": "${AuthCode.AUTH_INVALID_TOKEN.message}"}"""
        response.writer.write(json)
    }
}