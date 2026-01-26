package com.example.home_recipe.domain.auth.oauth2

class KakaoOAuth2UserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {
    override val provider: OAuth2Provider = OAuth2Provider.KAKAO
    private val kakaoAccount: Map<String, Any> = OAuth2AttributeValidator.optionalMap(attributes = attributes, key = OAuth2Constants.ATTRIBUTE_KAKAO_ACCOUNT)
    private val profile: Map<String, Any> = OAuth2AttributeValidator.optionalMap(attributes = kakaoAccount, key = OAuth2Constants.ATTRIBUTE_PROFILE)
    override val providerId: String = OAuth2AttributeValidator.requiredString(attributes = attributes, key = OAuth2Constants.ATTRIBUTE_ID)
    override val email: String = OAuth2AttributeValidator.requiredString(attributes = kakaoAccount, key = OAuth2Constants.ATTRIBUTE_EMAIL)
    override val name: String = OAuth2AttributeValidator.requiredString(attributes = profile, key = OAuth2Constants.ATTRIBUTE_NICKNAME)
}