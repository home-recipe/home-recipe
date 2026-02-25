package com.example.home_recipe.service.auth

import com.example.home_recipe.domain.auth.oauth2.AuthCodeEntry
import com.example.home_recipe.domain.auth.oauth2.OAuth2Constants
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.global.response.code.BaseCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*

@Component
class OAuth2AuthenticationSuccessHandler(
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
    private val authCodeCacheService: AuthCodeCacheService,
) : AuthenticationSuccessHandler {

    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    companion object {
        const val WEB_REDIRECT_URL = "https://recook.kr/login-callback"
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        try {
            val authorizationRequest = authorizationRequestRepository.loadAuthorizationRequest(request)
            val challenge = authorizationRequest
                ?.additionalParameters
                ?.get(HttpCookieOAuth2AuthorizationRequestRepository.PKCE_CHALLENGE_PARAM) as? String
                ?: throw BusinessException(
                    baseCode = AuthCode.AUTH_PKCE_CHALLENGE_MISSING,
                    status = HttpStatus.BAD_REQUEST
                )

            val principal: OAuth2User = authentication.principal as OAuth2User
            val email: String = principal.getAttribute(OAuth2Constants.EMAIL)
                ?: throw BusinessException(
                    baseCode = AuthCode.AUTH_OAUTH2_INVALID_USER_INFO,
                    status = HttpStatus.UNAUTHORIZED
                )

            val authorizationCode = UUID.randomUUID().toString()
            authCodeCacheService.store(authorizationCode, AuthCodeEntry(challenge = challenge, email = email))
            log.info("캐시에 인가 코드 저장 완료: {}", authorizationCode)

            authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)

            val redirectUrl = "$WEB_REDIRECT_URL?code=$authorizationCode"
            response.sendRedirect(redirectUrl)
        } catch (ex: BusinessException) {
            writeError(response = response, status = ex.status, code = ex.baseCode)
        }
    }

    private fun writeError(
        response: HttpServletResponse, status: HttpStatus, code: BaseCode
    ) {
        val entity = ApiResponse.error<Unit>(
            responseCode = code, status = status
        )

        response.status = entity.statusCode.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = SecurityResponseConstants.CHARACTER_ENCODING_UTF_8
        response.setHeader(
            SecurityResponseConstants.HEADER_CACHE_CONTROL,
            SecurityResponseConstants.HEADER_VALUE_NO_STORE
        )
        response.writer.write(objectMapper.writeValueAsString(entity.body))
        response.writer.flush()
    }
}
