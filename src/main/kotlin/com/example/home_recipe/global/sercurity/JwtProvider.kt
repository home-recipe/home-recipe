package com.example.home_recipe.global.sercurity

import com.example.home_recipe.global.response.code.UserCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-expiration}") private val accessExpiration: Long,
    @Value("\${jwt.refresh-expiration}") private val refreshExpiration: Long
) {
    fun getRefreshExpiration(): Long = refreshExpiration

    fun validateAccessToken(token: String?): UserCode {
        if (token.isNullOrBlank()) return UserCode.AUTH_ERROR_001

        return try {
            val key = getSigningKey()
            val parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()

            parser.parseClaimsJws(token)
            UserCode.AUTH_SUCCESS
        } catch (ex: ExpiredJwtException) {
            UserCode.AUTH_ERROR_002
        } catch (ex: Exception) {
            UserCode.AUTH_ERROR_003
        }
    }

    fun validateRefreshToken(token: String?): UserCode {
        if (token.isNullOrBlank()) return UserCode.AUTH_ERROR_004

        return try {
            val key = getSigningKey()
            val parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()

            parser.parseClaimsJws(token)
            UserCode.AUTH_SUCCESS
        } catch (ex: ExpiredJwtException) {
            UserCode.AUTH_ERROR_005
        } catch (ex: Exception) {
            UserCode.AUTH_ERROR_006
        }
    }

    fun getEmailFromToken(token: String): String {
        val key = getSigningKey()
        val parser = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()

        val claims = parser.parseClaimsJws(token)
        return claims.body.subject
    }

    fun generateAccessToken(email: String): String {
        return generateToken(email, accessExpiration)
    }

    fun generateRefreshToken(email: String): String {
        return generateToken(email, refreshExpiration)
    }

    private fun generateToken(email: String, expiration: Long): String {
        val now = System.currentTimeMillis()
        val key = getSigningKey()

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + expiration))
            .signWith(key)
            .compact()
    }

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }
}