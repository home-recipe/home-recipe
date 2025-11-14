package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.code.AuthCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.http.HttpStatus
import kotlin.test.Test

class BusinessExceptionTest {

    @Test
    @DisplayName("BusinessException 생성 시 필드 값이 올바르게 설정된다")
    fun businessException_생성_테스트() {
        // given
        val responseCode = AuthCode.AUTH_INVALID_TOKEN
        val status = HttpStatus.UNAUTHORIZED

        // when
        val exception = BusinessException(responseCode, status)

        // then
        assertThat(exception.baseCode).isEqualTo(responseCode)
        assertThat(exception.status).isEqualTo(status)
        assertThat(exception.message).isEqualTo(responseCode.message)
    }
}