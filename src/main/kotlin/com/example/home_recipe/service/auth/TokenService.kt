package com.example.home_recipe.service.auth

import com.example.home_recipe.domain.auth.RefreshToken
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
@Transactional
class TokenService(
    private val tokenRepository: RefreshTokenRepository,
) {
    fun synchronizeRefreshToken(user: User, refreshToken: String) {
        val existingToken: RefreshToken? = tokenRepository.findByUser(user).orElse(null)

        if (existingToken != null) {
            existingToken.updateRefreshToken(refreshToken)
        } else {
            tokenRepository.save(RefreshToken(user, refreshToken))
        }
    }

    fun getValidRefreshToken(refreshToken: String): RefreshToken {
        return tokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow {
                BusinessException(AuthCode.AUTH_INVALID_TOKEN, HttpStatus.UNAUTHORIZED)
            }
    }

    fun getRefreshTokenByUser(user: User): RefreshToken {
        return tokenRepository.findByUser(user)
            .orElseThrow {
                BusinessException(UserCode.LOGIN_ERROR_002, HttpStatus.UNAUTHORIZED)
            }
    }

    fun deleteRefreshToken(refreshToken: RefreshToken) {
        tokenRepository.delete(refreshToken)
    }
}