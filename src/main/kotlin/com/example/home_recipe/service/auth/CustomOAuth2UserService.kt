package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.user.dto.request.JoinRequest
import com.example.home_recipe.domain.auth.oauth2.OAuth2Constants
import com.example.home_recipe.domain.auth.oauth2.OAuth2UserInfoFactory
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.service.user.UserService
import org.hibernate.validator.internal.util.logging.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomOAuth2UserService(
    private val userService: UserService,
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User = super.loadUser(userRequest)
        val registrationId: String = userRequest.clientRegistration.registrationId
        val userInfo = OAuth2UserInfoFactory.from(registrationId, oAuth2User.attributes)
        val email: String = userInfo.email
        val name: String = userInfo.name
        val user: User = findOrCreateUser(email, name)
        val attributes: MutableMap<String, Any> = HashMap(oAuth2User.attributes)
        attributes[OAuth2Constants.ATTRIBUTE_LOCAL_EMAIL] = user.email
        val authorities = listOf(SimpleGrantedAuthority(OAuth2Constants.ROLE_PREFIX + user.role.name))

        return DefaultOAuth2User(authorities, attributes, OAuth2Constants.ATTRIBUTE_LOCAL_EMAIL)
    }

    private fun findOrCreateUser(
        email: String,
        name: String
    ): User {
        if (userService.isExistUser(email)) {
            return userService.getUser(email)
        }

        val randomPassword: String = UUID.randomUUID().toString()
        val joinRequest = JoinRequest(
                email = email,
                password = randomPassword,
                name = name)

        userService.join(joinRequest)

        return userService.getUser(email)
    }
}