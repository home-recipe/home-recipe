package com.example.home_recipe.domain.auth.oauth2

object OAuth2UserInfoFactory {
    fun from(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (registrationId.lowercase()) {
            "google" -> GoogleOAuth2UserInfo(attributes)
            "kakao" -> KakaoOAuth2UserInfo(attributes)
            else -> error("Unsupported oauth2 provider: $registrationId")
        }
    }
}