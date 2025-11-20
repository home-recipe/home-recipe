package com.example.home_recipe.domain.auth.config

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class ForbiddenHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        throw BusinessException(AuthCode.AUTH_FORBIDDEN, HttpStatus.FORBIDDEN)
    }
}