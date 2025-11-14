package com.example.home_recipe.service.auth

import com.example.home_recipe.domain.user.User
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var tokenRepository: RefreshTokenRepository
    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var tokenService: TokenService

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
        const val BEFORE_REFRESH_TOKEN = "beforeRefreshToken"
        const val AFTER_REFRESH_TOKEN = "afterRefreshToken"
    }

    @AfterEach
    fun deleteAll() {
        tokenRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("리프레시 토큰을 저장한다")
    fun 리프레시_토큰을_저장한다() {
        //given
        val user = userRepository.save(User(NAME, PASSWORD, EMAIL))
        val before = tokenRepository.findByUser_Email(EMAIL)

        //when
        tokenService.synchronizeRefreshToken(user, BEFORE_REFRESH_TOKEN)
        val after = tokenRepository.findByUser_Email(EMAIL)

        //then
        assertAll(
            { assertThat(before).isEmpty },
            { assertThat(after).isPresent }
        )
    }

    @Test
    @DisplayName("리프레시 토큰을 업데이트한다.")
    fun 리프레시_토큰을_업데이트한다() {
        //given
        val user = userRepository.save(User(NAME, PASSWORD, EMAIL))
        tokenService.synchronizeRefreshToken(user, BEFORE_REFRESH_TOKEN)
        val before = tokenRepository.findByUser_Email(EMAIL)

        //when
        tokenService.synchronizeRefreshToken(user, AFTER_REFRESH_TOKEN)
        val after = tokenRepository.findByUser_Email(EMAIL)

        //then
        assertThat(before).isNotEqualTo(after)
    }

    @Test
    @DisplayName("리프레시 토큰을 삭제한다.")
    fun 리프레시_토큰을_삭제한다() {
        //given
        val user = userRepository.save(User(NAME, PASSWORD, EMAIL))
        tokenService.synchronizeRefreshToken(user, BEFORE_REFRESH_TOKEN)
        val refreshToken = tokenRepository.findByUser_Email(EMAIL).get()

        //when
        tokenService.deleteRefreshToken(refreshToken)

        //then
        assertThat(tokenRepository.findByUser_Email(EMAIL)).isEmpty
    }

}