package com.example.home_recipe.repository

import com.example.home_recipe.domain.auth.RefreshToken
import com.example.home_recipe.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {
    @Autowired
    private lateinit var tokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
        const val REFRESH_TOKEN =  "refreshToken"
    }

    @Test
    @DisplayName("이메일로 유저를 찾고, 유저의 리프레쉬 토큰을 조회한다.")
    fun 이메일로_유저를_찾고_유저의_리프레쉬_토큰을_조회한다() {
        saveToken()
        val refreshToken = tokenRepository.findByUser_Email(EMAIL)
        assertThat(refreshToken).isPresent
    }

    @Test
    @DisplayName("리프레쉬 토큰을 조회한다.")
    fun 리프레쉬_토큰을_조회한다() {
        saveToken()
        val refreshToken = tokenRepository.findByRefreshToken(REFRESH_TOKEN)
        assertThat(refreshToken).isPresent
    }

    @Test
    @DisplayName("이메일로 유저를 찾을 수 없다면 리프레쉬 토큰이 조회되지 않는다.")
    fun 이메일로_유저를_찾을_수_없다면_리프레쉬_토큰이_조회되지_않는다() {
        saveToken()
        val refreshToken = tokenRepository.findByUser_Email("notExistEmail")
        assertThat(refreshToken).isEmpty
    }

    @Test
    @DisplayName("리프레쉬 토큰이 조회되지 않는다.")
    fun 리프레쉬_토큰을_조회되지_않는다() {
        saveToken()
        val refreshToken = tokenRepository.findByRefreshToken("notExistRefreshToken")
        assertThat(refreshToken).isEmpty
    }

    fun saveToken() {
        val user = userRepository.save(User(UserRepositoryTest.NAME, UserRepositoryTest.PASSWORD, UserRepositoryTest.EMAIL))
        tokenRepository.save(RefreshToken(user, REFRESH_TOKEN))
    }
}