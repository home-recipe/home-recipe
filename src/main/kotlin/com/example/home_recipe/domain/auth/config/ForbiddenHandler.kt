package com.example.home_recipe.domain.auth.config

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.ResponseDetail
import com.example.home_recipe.global.response.code.AuthCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class ForbiddenHandler : AccessDeniedHandler {

    companion object {
        const val CONTENT_TYPE = "application/json;charset=UTF-8"
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val authCode = AuthCode.AUTH_FORBIDDEN

        val body = ApiResponse(
            code = HttpStatus.FORBIDDEN.value(),
            message = authCode.message,
            response = ResponseDetail(
                code = authCode.code,
                data = null
            )
        )

        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = CONTENT_TYPE
        response.writer.write(
            ObjectMapper().writeValueAsString(body)
        )
    }
}