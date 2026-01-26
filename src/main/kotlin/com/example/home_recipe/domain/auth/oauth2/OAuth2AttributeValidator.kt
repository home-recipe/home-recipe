package com.example.home_recipe.domain.auth.oauth2

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import org.springframework.http.HttpStatus

object OAuth2AttributeValidator {

    fun requiredString(
        attributes: Map<String, Any>,
        key: String
    ): String {
        val value: String? =
            attributes[key]?.toString()

        if (value.isNullOrBlank()) {
            throw BusinessException(
                baseCode = AuthCode.AUTH_OAUTH2_INVALID_USER_INFO,
                status = HttpStatus.UNAUTHORIZED
            )
        }

        return value
    }

    @Suppress("UNCHECKED_CAST")
    fun optionalMap(
        attributes: Map<String, Any>,
        key: String
    ): Map<String, Any> {
        val value: Map<String, Any>? =
            attributes[key] as? Map<String, Any>

        return value ?: emptyMap()
    }
}
