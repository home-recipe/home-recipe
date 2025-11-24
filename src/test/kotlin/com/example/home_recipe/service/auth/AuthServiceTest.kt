package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.user.dto.request.JoinRequest
import com.example.home_recipe.controller.user.dto.request.LoginRequest
import com.example.home_recipe.controller.user.dto.response.JoinResponse
import com.example.home_recipe.domain.auth.config.JwtTokenProvider
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.user.UserService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {
    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var authService: AuthService
    @Autowired
    private lateinit var tokenRepository: RefreshTokenRepository
    @Autowired
    private lateinit var em : EntityManager

    companion object {
        const val NAME = "user"
        const val EMAIL = "user123@naver.com"
        const val PASSWORD = "password123"
    }

    ////// 해피 테스트
    @Test
    @DisplayName("로그인을 성공한다.")
    fun 로그인_성공_테스트() {
        //given
        createUser()
        val loginRequest = LoginRequest(EMAIL, PASSWORD)
        //when
        val loginResponse = authService.login(loginRequest)
        //then
        assertThat(loginResponse.accessToken).isNotNull()
    }

    @Test
    @DisplayName("DB에 저장된 리프레시 토큰이 없을 경우 새로운 토큰 정보를 저장한다.")
    fun DB에_저장된_리프레시_토큰이_없을_경우_새로운_토큰_정보_저장() {
        //given
        createUser()
        val loginRequest = LoginRequest(EMAIL, PASSWORD)
        val before = tokenRepository.findByUser_Email(EMAIL)
        //when
        val loginResponse = authService.login(loginRequest)
        val after = tokenRepository.findByUser_Email(EMAIL)
        //then
        assertAll(
            { assertThat(before).isEmpty },
            { assertThat(after).isPresent }
        )
    }

    @Test
    @DisplayName("DB에 저장된 리프레시 토큰이 있을 경우 새로운 토큰 정보를 저장한다.")
    fun DB에_저장된_리프레시_토큰이_있을_경우_새로운_토큰_정보_저장() {
        //given
        createUser()
        val loginRequest = LoginRequest(EMAIL, PASSWORD)
        //when
        authService.login(loginRequest)
        val before = tokenRepository.findByUser_Email(EMAIL)
        authService.login(loginRequest)
        em.flush()
        em.clear()
        val after = tokenRepository.findByUser_Email(EMAIL)
        //then
        assertThat(before).isNotEqualTo(after)
    }

    @Test
    @DisplayName("Access Token을 재발급한다.")
    fun Access_Token을_재발급한다() {
        //given
        createUser()
        val loginRequest = LoginRequest(EMAIL, PASSWORD)
        //when
        val after = authService.reissueAccessToken(EMAIL).accessToken
        //then
        assertThat(after).isNotNull()
    }

    ////// 예외 테스트
    @Test
    @DisplayName("DB에 저장되지 않은 EMAIL로 로그인 시도할 시 예외가 반환된다.")
    fun DB에_저장되지_않은_EMAIL로_로그인_시도할_시_예외가_반환된다() {
        //given
        createUser()
        val loginRequest = LoginRequest("notEmail@naver.com", PASSWORD)
        //when & then
        Assertions.assertThatThrownBy { authService.login(loginRequest) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("존재하지 않는 이메일")
    }

    @Test
    @DisplayName("비밀번호가 불일치한 상태로 로그인 시도할 시 예외가 반환된다.")
    fun 비밀번호가_불일치한_상태로_로그인_시도할_시_예외가_반환된다() {
        //given
        createUser()
        val loginRequest = LoginRequest(EMAIL, "notPassword")
        //when & then
        Assertions.assertThatThrownBy { authService.login(loginRequest) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("비밀번호 불일치")
    }

    fun createUser(): JoinResponse {
        val joinRequest = JoinRequest(NAME, PASSWORD, EMAIL)
        return userService.join(joinRequest)
    }
}