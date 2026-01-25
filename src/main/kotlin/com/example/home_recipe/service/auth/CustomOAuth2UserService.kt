package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.user.dto.request.JoinRequest
import com.example.home_recipe.domain.auth.oauth2.OAuth2UserInfoFactory
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.service.user.UserService
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
        val oAuth2User = super.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        val userInfo = OAuth2UserInfoFactory.from(registrationId, oAuth2User.attributes)

        val email = userInfo.email
            ?: throw OAuth2AuthenticationException("Email not provided from $registrationId")

        val name = userInfo.name ?: "USER"

        val user = findOrCreateUser(email, name)

        val attributes = HashMap(oAuth2User.attributes)
        attributes["localEmail"] = user.email

        val authorities =
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        return DefaultOAuth2User(authorities, attributes, "localEmail")
    }

    private fun findOrCreateUser(email: String, name: String): User {
        if (userService.isExistUser(email)) {
            return userService.getUser(email)
        }

        val randomPassword = UUID.randomUUID().toString()

        userService.join(
            JoinRequest(
                email = email,
                password = randomPassword,
                name = name
            )
        )

        return userService.getUser(email)
    }
}