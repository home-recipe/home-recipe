package com.example.home_recipe.service.user

import com.example.home_recipe.controller.user.dto.JoinRequest
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
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserCodeServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var userService: UserService

    //////해피 테스트
    @Test
    @DisplayName("회원가입 성공 테스트")
    fun 회원가입_성공_테스트() {
        //given
        val request = JoinRequest(
            name = "userName",
            loginId = "testUser",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )
        val encryptedPassword = "encodedPassword"
        val savedUser = User(
            loginId = request.loginId,
            name = request.name,
            password = encryptedPassword,
            email =  request.email,
            phoneNumber = request.phoneNumber
        )

        whenever(passwordEncoder.encode(any())).thenReturn(encryptedPassword)
        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        whenever(userRepository.existsByLoginId(any())).thenReturn(false)
        whenever(userRepository.existsByPhoneNumber(any())).thenReturn(false)
        whenever(userRepository.save(any())).thenReturn(savedUser)

        //when
        val response = userService.join(request)

        //then
        Assertions.assertThat(response.role).isEqualTo(Role.USER.name)
        Assertions.assertThat(response.loginId).isEqualTo(request.loginId)
    }

    ///////예외 테스트
    @Test
    @DisplayName("로그인ID가 존재할 경우 회원가입 실패")
    fun 로그인ID가_존재할_경우_회원가입_실패() {
        //given
        val request = JoinRequest(
            name = "userName",
            loginId = "testUser",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )
        val encryptedPassword = "encodedPassword"
        whenever(passwordEncoder.encode(any())).thenReturn(encryptedPassword)
        whenever(userRepository.existsByLoginId(any())).thenReturn(true)

        // when & then
        assertThatThrownBy {
            userService.join(request)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(UserCode.SIGNUP_ERROR_007.message)
    }

    @Test
    @DisplayName("email이 존재할 경우 회원가입 실패")
    fun email이_존재할_경우_회원가입_실패() {
        //given
        val request = JoinRequest(
            name = "userName",
            loginId = "testUser",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )
        val encryptedPassword = "encodedPassword"
        whenever(passwordEncoder.encode(any())).thenReturn(encryptedPassword)
        whenever(userRepository.existsByEmail(any())).thenReturn(true)
        whenever(userRepository.existsByLoginId(any())).thenReturn(false)

        // when & then
        assertThatThrownBy {
            userService.join(request)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(UserCode.SIGNUP_ERROR_005.message)
    }

    @Test
    @DisplayName("phoneNumber가 존재할 경우 회원가입 실패")
    fun phoneNumber가_존재할_경우_회원가입_실패() {
        //given
        val request = JoinRequest(
            name = "userName",
            loginId = "testUser",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "01012345678"
        )
        val encryptedPassword = "encodedPassword"
        whenever(passwordEncoder.encode(any())).thenReturn(encryptedPassword)
        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        whenever(userRepository.existsByLoginId(any())).thenReturn(false)
        whenever(userRepository.existsByPhoneNumber(any())).thenReturn(true)

        // when & then
        assertThatThrownBy {
            userService.join(request)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(UserCode.SIGNUP_ERROR_008.message)
    }
}