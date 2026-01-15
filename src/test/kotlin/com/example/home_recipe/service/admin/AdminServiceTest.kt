package com.example.home_recipe.service.admin

import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.UserRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var adminService: AdminService

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
    }

    @Test
    fun `모든 유저 조회 테스트`() {
        val mockUser = User(NAME, EMAIL, PASSWORD)
        ReflectionTestUtils.setField(mockUser, "id", 1L)

        val mockUsers = listOf(mockUser)
        whenever(userRepository.findAll()).thenReturn(mockUsers)

        // When: 서비스 실행
        val result = adminService.getAllUsers()

        // Then: 결과 검증
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("user")
    }

    @Test
    fun `유저 역할 수정 테스트 - 성공`() {
        // Given: 특정 유저가 존재한다고 가정
        val mockUser = User(NAME, EMAIL, PASSWORD)
        val beforeRole = mockUser.role;
        ReflectionTestUtils.setField(mockUser, "id", 1L)
        whenever(userRepository.findById(any())).thenReturn(Optional.of(mockUser))

        // When
        val result = adminService.updateUserRole(1L, Role.ADMIN)
        assertThat(beforeRole).isEqualTo(Role.USER)
        assertThat(result.role).isEqualTo(Role.ADMIN)
    }

    @Test
    fun `유저 역할 수정 테스트 - 유저가 없을 때 예외 발생`() {
        // Given: 유저를 찾지 못했을 때
        whenever(userRepository.findById(any())).thenReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<BusinessException> {
            adminService.updateUserRole(1L, Role.ADMIN)
        }

        assertThat(exception.baseCode).isEqualTo(UserCode.LOGIN_ERROR_002)
    }
}