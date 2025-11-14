package com.example.home_recipe.service.user

import com.example.home_recipe.controller.dto.user.dto.request.JoinRequest
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.UserRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    private lateinit var userService: UserService

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
    }

    //////해피 테스트
    @Test
    @DisplayName("회원가입 성공 테스트")
    fun 회원가입_성공_테스트() {
        //given
        val joinRequest = JoinRequest(NAME, PASSWORD, EMAIL)

        //when
        val response = userService.join(joinRequest)

        //then
        Assertions.assertThat(response.email).isEqualTo(EMAIL)
    }

    ///////예외 테스트
    @Test
    @DisplayName("email이 존재할 경우 회원가입 실패")
    fun email이_존재할_경우_회원가입_실패() {
        //given
        userService.join(JoinRequest(NAME, PASSWORD, EMAIL))
        val joinRequest = JoinRequest(NAME, PASSWORD, EMAIL)

        // when & then
        assertThatThrownBy {
            userService.join(joinRequest)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(UserCode.SIGNUP_ERROR_005.message)
    }
}