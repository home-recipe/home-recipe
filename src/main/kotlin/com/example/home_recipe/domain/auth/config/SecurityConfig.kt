package com.example.home_recipe.domain.auth.config

import org.apache.catalina.core.ApplicationFilterChain
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${jwt.secret}") private val secretKey: String,
    private val unauthorizedHandler: UnauthorizedHandler,
    private val forbiddenHandler: ForbiddenHandler
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
    fun jwtAuthenticationConverter(): Converter<Jwt, out AbstractAuthenticationToken> {
        return Converter { jwt ->
            val email = jwt.getClaim<String>(EMAIL) ?: jwt.subject
            UsernamePasswordAuthenticationToken(email, null, emptyList())
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
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
                it.requestMatchers(
                    "/api/user/**",
                    "/api/auth/login",
                    "/api/auth/reissue"
                ).permitAll()

                it.anyRequest().authenticated()
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
