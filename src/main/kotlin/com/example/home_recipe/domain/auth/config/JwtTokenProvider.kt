package com.example.home_recipe.domain.auth.config

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-expiration}") private val accessTokenValidity: Long,
    @Value("\${jwt.refresh-expiration}") private val refreshTokenValidity: Long
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createAccessToken(email: String): String {
        return createToken(email, accessTokenValidity)
    }

    fun createRefreshToken(email: String): String {
        return createToken(email, refreshTokenValidity)
    }

    private fun createToken(email: String, validity: Long): String {
        val now = Date()
        val expiration = Date(now.time + validity)
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getRemainingSeconds(token: String): Long {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val exp = claims.expiration.toInstant()
        val now = Instant.now()

        return Duration.between(now, exp).seconds.coerceAtLeast(0)
    }

    fun validateToken(token: String) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        } catch (e: ExpiredJwtException) {
            throw BusinessException(AuthCode.AUTH_REFRESH_EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED)
        } catch (e: JwtException) {
            throw BusinessException(AuthCode.AUTH_REFRESH_INVALID_TOKEN, HttpStatus.UNAUTHORIZED)
        } catch (e: IllegalArgumentException) {
            throw BusinessException(AuthCode.AUTH_REFRESH_INVALID_TOKEN, HttpStatus.UNAUTHORIZED)
        }

    }

    fun getEmail(token: String): String {
        val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        return claims.body.subject
    }
}