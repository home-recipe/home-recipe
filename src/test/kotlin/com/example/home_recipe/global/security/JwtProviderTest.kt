package com.example.home_recipe.global.security

import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.global.sercurity.JwtProvider
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class JwtProviderTest {
    private lateinit var jwtProvider: JwtProvider

    @BeforeEach
    fun setup() {
        jwtProvider = JwtProvider(
            secretKey = "my-super-secret-key-for-test-123456",
            accessExpiration = 3600000,
            refreshExpiration = 1209600000
        )
    }

    //////해피 테스트
    @Test
    @DisplayName("refreshToken의 만료시간을 반환한다.")
    fun refreshToken_만료시간_반환() {
        assertThat(jwtProvider.getRefreshExpiration()).isEqualTo(1209600000)
    }

    @Test
    @DisplayName("accessToken을 생성한다.")
    fun accessToken_생성() {
        //given
        val email = "user1232@naver.com"

        //when
        val token = jwtProvider.generateAccessToken(email)

        //then
        val parsedEmail = jwtProvider.getEmailFromToken(token)
        val result = jwtProvider.validateAccessToken(token)
        assertThat(parsedEmail).isEqualTo(email)
        assertThat(result).isEqualTo(UserCode.AUTH_SUCCESS)
    }

    //////예외 테스트
    @Test
    @DisplayName("위조된 accessToken은 Code : AUTH_ERROR_003 반환한다.")
    fun 위조된_accessToken_검증_실패() {
        // given
        val email = "user1232@naver.com"

        // 원래 서버에서 사용하는 provider (정상 키)
        val legitProvider = JwtProvider(
            secretKey = "this-is-a-real-secret-key-for-test-1234567890",
            accessExpiration = 3600000,
            refreshExpiration = 1209600000
        )

        // 해커가 비슷하게 위조한 provider (다른 키)
        val fakeProvider = JwtProvider(
            secretKey = "this-is-a-fake-secret-key-for-attack-0000000000",
            accessExpiration = 3600000,
            refreshExpiration = 1209600000
        )

        // 해커가 만든 위조 토큰
        val counterfeitToken = fakeProvider.generateAccessToken(email)

        // when
        val result = legitProvider.validateAccessToken(counterfeitToken)

        // then
        assertThat(result).isEqualTo(UserCode.AUTH_ERROR_003)
    }

    @Test
    @DisplayName("만료된 accessToken은 Code : AUTH_ERROR_002 반환한다.")
    fun 만료된_accessToken_검증_실패() {
        // given
        val email = "user123@naver.com"

        // 매우 짧은 만료시간 (1ms)
        val shortLivedProvider = JwtProvider(
            secretKey = "this-is-a-test-secret-key-for-expired-token-1234567890",
            accessExpiration = 1,
            refreshExpiration = 1209600000
        )

        // 토큰 생성
        val expiredToken = shortLivedProvider.generateAccessToken(email)

        // 조금 기다려서 만료 유도
        Thread.sleep(10)

        // when
        val result = shortLivedProvider.validateAccessToken(expiredToken)

        // then
        assertThat(result).isEqualTo(UserCode.AUTH_ERROR_002)
    }
}