package com.example.home_recipe.service.auth


import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class AuthHelper(
    @Value("\${app.redirect.web-url}")
    private val webRedirectUrl: String,

    @Value("\${app.redirect.mobile-url}")
    private val mobileRedirectUrl: String,
) {

    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val CLIENT_TYPE_HEADER = "X-Client-Type"
        const val REFRESH_TOKEN_HEADER = "X-Refresh-Token"
        const val MAX_AGE = 604800
        const val WEB = "WEB"
        const val MOBILE = "MOBILE"
    }

    fun resolveClientType(request: HttpServletRequest): String {
        return (request.getHeader(CLIENT_TYPE_HEADER) ?: WEB).uppercase()
    }

    fun extractRefreshToken(request: HttpServletRequest): String? {
        val clientType = resolveClientType(request)
        return if (clientType == MOBILE) {
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

        val baseUrl = if(clientType.uppercase() == MOBILE) mobileRedirectUrl else webRedirectUrl

        val uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam(ACCESS_TOKEN, accessToken)

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
