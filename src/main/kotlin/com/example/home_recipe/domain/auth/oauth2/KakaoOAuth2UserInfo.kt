package com.example.home_recipe.domain.auth.oauth2

@Suppress("UNCHECKED_CAST")
class KakaoOAuth2UserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {

    override val provider = OAuth2Provider.KAKAO

    override val providerId: String =
        attributes["id"]?.toString() ?: error("Missing kakao id")

    private val kakaoAccount: Map<String, Any> =
        (attributes["kakao_account"] as? Map<String, Any>).orEmpty()

    private val profile: Map<String, Any> =
        (kakaoAccount["profile"] as? Map<String, Any>).orEmpty()

    override val email: String? = kakaoAccount["email"]?.toString()
    override val name: String? = profile["nickname"]?.toString()
}