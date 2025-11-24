package com.example.home_recipe.global.response

import com.example.home_recipe.global.response.code.BaseCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ApiResponse<T>(
    val code: Int,
    val message: String,
    val response: ResponseDetail<T?>
) {
    companion object {
        fun <T> success(
            data: T? = null,
            responseCode: BaseCode,
            status: HttpStatus = HttpStatus.OK
        ): ResponseEntity<ApiResponse<T>> {
            val body = ApiResponse(
                code = status.value(),
                message = responseCode.message,
                response = ResponseDetail(
                    code = responseCode.code,
                    data = data
                )
            )
            return ResponseEntity.status(status).body(body)
        }

        fun <T> error(
            data: T? = null,
            responseCode: BaseCode,
            status: HttpStatus
        ): ResponseEntity<ApiResponse<T>> {
            val body = ApiResponse(
                code = status.value(),
                message = responseCode.message,
                response = ResponseDetail(
                    code = responseCode.code,
                    data = data
                )
            )
            return ResponseEntity.status(status).body(body)
        }
    }
}