package com.example.home_recipe.global.exception

import com.example.home_recipe.global.response.ResponseCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.http.HttpStatus
import kotlin.test.Test

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    @DisplayName("BusinessException 발생 시 ApiResponse.error를 반환한다")
    fun handleBusinessException_테스트() {
        // given
        val exception = BusinessException(
            responseCode = ResponseCode.AUTH_ERROR_001,
            status = HttpStatus.UNAUTHORIZED
        )

        // when
        val response = handler.handleBusinessException(exception)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.body!!.response.code).isEqualTo(ResponseCode.AUTH_ERROR_001.code)
        assertThat(response.body!!.message).isEqualTo(ResponseCode.AUTH_ERROR_001.message)
    }
}