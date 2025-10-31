package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.dto.auth.dto.TokenDto
import com.example.home_recipe.controller.dto.user.dto.LoginRequest
import com.example.home_recipe.domain.auth.RefreshToken
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.global.sercurity.JwtProvider
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var jwtProvider: JwtProvider
    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder
    @InjectMocks
    private lateinit var authService: AuthService

    @Test
    @DisplayName("로그인 성공 시 refresh/access 토큰 발급 및 refresh 토큰이 저장된다.")
    fun 로그인_성공_테스트() {
        //given
        val request = LoginRequest(
            loginId = "user123",
            password = "password123"
        )
        val user = User(
            name = "user",
            loginId = "user123",
            password = "password123",
            email = "user123@naver.com",
            phoneNumber = "01011112222",
            role = Role.USER
        )
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val email = "user123@naver.com"
        val expiresAt = LocalDateTime.now().plusSeconds(3600)

        val refreshEntity = RefreshToken(
            email = email,
            token = refreshToken,
            expiresAt = expiresAt
        )
        whenever(userRepository.findByLoginId(any())).thenReturn(Optional.of(user))
        whenever(passwordEncoder.matches(any(), any())).thenReturn(true)
        whenever(jwtProvider.generateAccessToken(any())).thenReturn(accessToken)
        whenever(jwtProvider.generateRefreshToken(any())).thenReturn(refreshToken)
        whenever(jwtProvider.getRefreshExpiration()).thenReturn(3600_000L)
        whenever(refreshTokenRepository.save(any<RefreshToken>()))
            .thenReturn(refreshEntity)


        //when
        val tokenDto = authService.login(request)

        //then
        Assertions.assertThat(tokenDto.refreshToken).isEqualTo("refreshToken")
    }

    @Test
    @DisplayName("refreshToken 성공 테스트")
    fun refreshToken_성공_테스트() {
        // given
        val oldRefreshToken = "old-refresh-token"
        val newAccessToken = "new-access-token"
        val newRefreshToken = "new-refresh-token"
        val email = "user123@naver.com"
        val expiresAt = LocalDateTime.now().plusSeconds(3600)

        val request = TokenDto(
            accessToken = "expired-access-token",
            refreshToken = oldRefreshToken
        )

        val savedEntity = RefreshToken(email, oldRefreshToken, expiresAt)
        val newEntity = RefreshToken(email, newRefreshToken, expiresAt)

        whenever(jwtProvider.validateRefreshToken(oldRefreshToken))
            .thenReturn(UserCode.AUTH_SUCCESS)
        whenever(jwtProvider.getEmailFromToken(oldRefreshToken))
            .thenReturn(email)
        whenever(refreshTokenRepository.findByEmail(email))
            .thenReturn(Optional.of(savedEntity))
        whenever(jwtProvider.generateAccessToken(email))
            .thenReturn(newAccessToken)
        whenever(jwtProvider.generateRefreshToken(email))
            .thenReturn(newRefreshToken)
        whenever(jwtProvider.getRefreshExpiration())
            .thenReturn(3600_000L)
        whenever(refreshTokenRepository.save(any<RefreshToken>()))
            .thenReturn(newEntity)


        // when
        val result = authService.refreshToken(request)

        // then
        Assertions.assertThat(result.accessToken).isEqualTo(newAccessToken)
        Assertions.assertThat(result.refreshToken).isEqualTo(newRefreshToken)
        verify(refreshTokenRepository).save(any())
    }
}