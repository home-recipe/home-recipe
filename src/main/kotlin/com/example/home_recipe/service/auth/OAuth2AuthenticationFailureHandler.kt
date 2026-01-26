package com.example.home_recipe.service.auth

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler : AuthenticationFailureHandler {

    private val objectMapper =
        ObjectMapper()

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val entity = ApiResponse.error<Unit>(
                responseCode = AuthCode.AUTH_OAUTH2_LOGIN_FAILED,
                status = HttpStatus.UNAUTHORIZED)

        response.status = entity.statusCode.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = SecurityResponseConstants.CHARACTER_ENCODING_UTF_8

        response.setHeader(
            SecurityResponseConstants.HEADER_CACHE_CONTROL,
            SecurityResponseConstants.HEADER_VALUE_NO_STORE)

        val body = entity.body

        response.writer.write(objectMapper.writeValueAsString(body))
        response.writer.flush()
    }
}