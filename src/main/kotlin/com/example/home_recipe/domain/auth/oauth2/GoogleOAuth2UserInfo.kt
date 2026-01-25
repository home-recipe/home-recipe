package com.example.home_recipe.domain.auth.oauth2

class GoogleOAuth2UserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {

    override val provider = OAuth2Provider.GOOGLE
    override val providerId: String = attributes["sub"]?.toString() ?: error("Missing sub")
    override val email: String? = attributes["email"]?.toString()
    override val name: String? = attributes["name"]?.toString()
}