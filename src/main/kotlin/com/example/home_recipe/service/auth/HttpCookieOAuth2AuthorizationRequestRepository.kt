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
class HttpCookieOAuth2AuthorizationRequestRepository :
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    companion object {
        private const val COOKIE_NAME = "OAUTH2_AUTH_REQUEST"
        private const val COOKIE_EXPIRE_SECONDS = 180
        private const val COOKIE_PATH = "/"
        private const val EMPTY_VALUE = ""
    }

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val cookie: Cookie = request.cookies?.firstOrNull { it.name == COOKIE_NAME } ?: return null
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

        val value: String = serialize(authorizationRequest)

        addCookie(response, value)
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): OAuth2AuthorizationRequest? {
        val authRequest: OAuth2AuthorizationRequest? = loadAuthorizationRequest(request)

        deleteCookie(response)

        return authRequest
    }

    fun removeAuthorizationRequestCookies(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        deleteCookie(response)
    }

    private fun addCookie(response: HttpServletResponse, value: String) {
        // 기존 addCookie 대신 Set-Cookie를 직접 써서 SameSite 적용
        response.addHeader(
            "Set-Cookie",
            "OAUTH2_AUTH_REQUEST=$value; Path=/; Max-Age=$COOKIE_EXPIRE_SECONDS; HttpOnly; Secure; SameSite=None"
        )
    }

    private fun deleteCookie(response: HttpServletResponse) {
        response.addHeader(
            "Set-Cookie",
            "OAUTH2_AUTH_REQUEST=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None"
        )
    }

    private fun serialize(obj: OAuth2AuthorizationRequest): String {
        val bytes: ByteArray = SerializationUtils.serialize(obj)

        return Base64.getUrlEncoder().encodeToString(bytes)
    }

    private fun deserialize(value: String): OAuth2AuthorizationRequest {
        val bytes: ByteArray = Base64.getUrlDecoder().decode(value)

        return SerializationUtils.deserialize(bytes) as OAuth2AuthorizationRequest
    }
}