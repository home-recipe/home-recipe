package com.example.home_recipe.service.auth

import com.example.home_recipe.domain.auth.oauth2.SerializationUtils
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
class HttpCookieOAuth2AuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    companion object {
        private const val COOKIE_NAME = "OAUTH2_AUTH_REQUEST"
        private const val COOKIE_EXPIRE_SECONDS = 180
    }

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val cookie = request.cookies?.firstOrNull { it.name == COOKIE_NAME } ?: return null
        return deserialize(cookie.value)
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (authorizationRequest == null) {
            deleteCookie(response)
            return
        }
        val value = serialize(authorizationRequest)
        addCookie(response, value)
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): OAuth2AuthorizationRequest? {
        val authRequest = loadAuthorizationRequest(request)
        deleteCookie(response)
        return authRequest
    }

    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        deleteCookie(response)
    }

    private fun addCookie(response: HttpServletResponse, value: String) {
        val cookie = Cookie(COOKIE_NAME, value).apply {
            isHttpOnly = true
            path = "/"
            maxAge = COOKIE_EXPIRE_SECONDS
        }
        response.addCookie(cookie)
    }

    private fun deleteCookie(response: HttpServletResponse) {
        val cookie = Cookie(COOKIE_NAME, "").apply {
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
    }

    private fun serialize(obj: OAuth2AuthorizationRequest): String {
        val bytes = SerializationUtils.serialize(obj)
        return Base64.getUrlEncoder().encodeToString(bytes)
    }

    private fun deserialize(value: String): OAuth2AuthorizationRequest {
        val bytes = Base64.getUrlDecoder().decode(value)
        return SerializationUtils.deserialize(bytes) as OAuth2AuthorizationRequest
    }
}