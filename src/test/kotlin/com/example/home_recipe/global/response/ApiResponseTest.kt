package com.example.home_recipe.global.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ApiResponseTest {

    @Test
    @DisplayName("success()가 올바른 ApiResponse를 반환한다.")
    fun success_응답_테스트() {
        //given
        val data = "data"
        val responseCode = ResponseCode.AUTH_SUCCESS

        //when
        val response: ResponseEntity<ApiResponse<String>> =
            ApiResponse.success(data, responseCode, HttpStatus.OK)

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.message).isEqualTo(responseCode.message)
        assertThat(response.body!!.response.code).isEqualTo(responseCode.code)
        assertThat(response.body!!.response.data).isEqualTo(data)
    }

    @Test
    @DisplayName("error()가 올바른 ApiResponse를 반환한다")
    fun error_응답_테스트() {
        // given
        val responseCode = ResponseCode.AUTH_ERROR_001

        // when
        val response: ResponseEntity<ApiResponse<String>> =
            ApiResponse.error(responseCode, HttpStatus.UNAUTHORIZED)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.code).isEqualTo(401)
        assertThat(response.body!!.message).isEqualTo(responseCode.message)
        assertThat(response.body!!.response.code).isEqualTo(responseCode.code)
        assertThat(response.body!!.response.data).isNull()
    }
}