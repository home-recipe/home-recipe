package com.example.home_recipe.domain.auth.oauth2

enum class OAuth2Provider(
    val registrationId: String
) {
    GOOGLE("google"),
    KAKAO("kakao");

    companion object {
        fun from(registrationId: String): OAuth2Provider? {
            val normalizedRegistrationId: String = registrationId.lowercase()
            return entries.firstOrNull { it.registrationId == normalizedRegistrationId }
        }
    }
}