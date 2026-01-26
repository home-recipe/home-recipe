package com.example.home_recipe.domain.auth.oauth2

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import org.springframework.http.HttpStatus

object OAuth2UserInfoFactory {

    fun from(
        registrationId: String,
        attributes: Map<String, Any>
    ): OAuth2UserInfo {
        val provider: OAuth2Provider = OAuth2Provider.from(registrationId) ?: throw BusinessException(baseCode = AuthCode.AUTH_OAUTH2_LOGIN_FAILED, status = HttpStatus.UNAUTHORIZED)

        return when (provider) {
            OAuth2Provider.GOOGLE -> GoogleOAuth2UserInfo(attributes)
            OAuth2Provider.KAKAO -> KakaoOAuth2UserInfo(attributes)
        }
    }
}