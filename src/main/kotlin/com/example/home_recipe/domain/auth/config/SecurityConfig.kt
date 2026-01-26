package com.example.home_recipe.domain.auth.config

import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.service.auth.CustomOAuth2UserService
import com.example.home_recipe.service.auth.HttpCookieOAuth2AuthorizationRequestRepository
import com.example.home_recipe.service.auth.OAuth2AuthenticationFailureHandler
import com.example.home_recipe.service.auth.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${jwt.secret}") private val secretKey: String,
    private val unauthorizedHandler: UnauthorizedHandler,
    private val forbiddenHandler: ForbiddenHandler,
    private val oAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2FailureHandler: OAuth2AuthenticationFailureHandler,
    private val authorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
) {

    companion object {
        const val JWT_SIGNATURE_ALGORITHM = "HmacSHA256"
        const val EMAIL = "email"
        const val AUTHORIZATION = "authorization"
        const val BEARER = "Bearer "
        const val BEARER_LENGTH = 7;
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val key = SecretKeySpec(secretKey.toByteArray(), JWT_SIGNATURE_ALGORITHM)
        return NimbusJwtDecoder.withSecretKey(key).build()
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        return Converter { jwt ->
            val email = jwt.getClaim<String>(EMAIL) ?: jwt.subject
            val rawRole = jwt.getClaim<String>("role") ?: "USER"

            val authority = if (rawRole.startsWith("ROLE_")) rawRole else "ROLE_$rawRole"
            val authorities = listOf(SimpleGrantedAuthority(authority))

            JwtAuthenticationToken(jwt, authorities, email)
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                it.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                it.requestMatchers(
                    "/api/user/**",
                    "/api/auth/reissue",
                    "/api/auth/login",
                    "/actuator/**"
                ).permitAll()
                it.requestMatchers(
                    "/api/admin/**"
                ).hasRole(Role.ADMIN.name)
                it.anyRequest().authenticated()
            }

            .oauth2Login { oauth2 ->
                oauth2
                    .authorizationEndpoint { endpoint ->
                        endpoint.authorizationRequestRepository(authorizationRequestRepository)
                    }
                    .userInfoEndpoint { userInfo ->
                        userInfo.userService(oAuth2UserService)
                    }
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
            }

            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .authenticationEntryPoint(unauthorizedHandler)
                    .jwt { jwt ->
                        jwt.decoder(jwtDecoder())
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                    }
            }

            .exceptionHandling {
                it.authenticationEntryPoint(unauthorizedHandler)
                it.accessDeniedHandler(forbiddenHandler)
            }

        return http.build()
    }
}
