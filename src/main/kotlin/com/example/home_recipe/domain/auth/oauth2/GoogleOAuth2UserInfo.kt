package com.example.home_recipe.domain.auth.oauth2

class GoogleOAuth2UserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {
    override val provider: OAuth2Provider = OAuth2Provider.GOOGLE
    override val providerId: String = OAuth2AttributeValidator.requiredString(attributes = attributes, key = OAuth2Constants.ATTRIBUTE_SUB)
    override val email: String = OAuth2AttributeValidator.requiredString(attributes = attributes, key = OAuth2Constants.EMAIL)
    override val name: String = OAuth2AttributeValidator.requiredString(attributes = attributes, key = OAuth2Constants.ATTRIBUTE_NAME)
}