package com.example.home_recipe.repository

import com.example.home_recipe.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
    }

    /////// 해피 테스트
    @Test
    @DisplayName("유저를 저장한다.")
    fun 유저를_저장한다() {
        val user = userRepository.save(User(NAME, PASSWORD, EMAIL))
        assertThat(user.id).isNotNull()
    }

    @Test
    @DisplayName("저장된 이메일이 존재할 경우 true를 반환한다.")
    fun 저장된_이메일_존재할_경우_true를_반환한다() {
        saveUser()
        assertThat(userRepository.existsByEmail(EMAIL)).isTrue()
    }

    @Test
    @DisplayName("유저를 조회한다.")
    fun 유저를_조회한다() {
        saveUser()
        assertThat(userRepository.existsByEmail(EMAIL)).isNotNull()
    }

    ////// 예외 테스트
    @Test
    @DisplayName("저장되지 않은 이메일로 조회를 시도할 경우 false를 반환한다")
    fun 저장되지_않은_이메일로_조회_시도_시_false_발환() {
        saveUser()
        assertThat(userRepository.existsByEmail("notExistEmail@naver.com")).isFalse()
    }

    fun saveUser() {
        userRepository.save(User(NAME, PASSWORD, EMAIL))
    }
}