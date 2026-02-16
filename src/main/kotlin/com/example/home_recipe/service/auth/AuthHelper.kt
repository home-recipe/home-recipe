package com.example.home_recipe.service.auth


import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class AuthHelper {

    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val WEB_REDIRECT_URL = "https://recook.kr/login-callback"
        const val MOBILE_REDIRECT_URL = "recook://login-callback"
        const val CLIENT_TYPE_HEADER = "X-Client-Type"
        const val REFRESH_TOKEN_HEADER = "X-Refresh-Token"
        const val MAX_AGE = 604800
        const val WEB = "WEB"
        const val MOBILE = "MOBILE"
    }

    fun extractRefreshToken(request: HttpServletRequest): String? {
        val clientType = request.getHeader(CLIENT_TYPE_HEADER) ?: WEB
        return if (clientType.uppercase() == MOBILE) {
            request.getHeader(REFRESH_TOKEN_HEADER)
        } else {
            request.cookies?.find { it.name == REFRESH_TOKEN }?.value
        }
    }

    fun setRefreshTokenCookie(response: HttpServletResponse, refreshToken: String) {
        val cookie = createRefreshTokenCookie(refreshToken)
        response.addCookie(cookie)
    }

    fun buildResponse(
        accessToken: String,
        refreshToken: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        clientTypeFromCookie: String? = null
    ) {
        val clientType = request.getHeader("X-Client-Type")
            ?: clientTypeFromCookie
            ?: WEB

        val baseUrl = if(clientType.uppercase() == MOBILE) MOBILE_REDIRECT_URL else WEB_REDIRECT_URL
        println(">>>>>>>>AuthHelper : " + baseUrl)

        val uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam(ACCESS_TOKEN, accessToken)
        println(">>>>>>>>AuthHelper uriBuilder result : " + uriBuilder)

        if (clientType.uppercase() == MOBILE) {
            val targetUrl = uriBuilder
                .queryParam(REFRESH_TOKEN, refreshToken)
                .build()
                .toUriString()
            response.sendRedirect(targetUrl)
        } else {
            val refreshTokenCookie = createRefreshTokenCookie(refreshToken)
            response.addCookie(refreshTokenCookie)

            val targetUrl = uriBuilder.build().toUriString()
            response.sendRedirect(targetUrl)
        }
    }

    private fun createRefreshTokenCookie(refreshToken: String): Cookie {
        return Cookie(REFRESH_TOKEN, refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = MAX_AGE
        }
    }
}