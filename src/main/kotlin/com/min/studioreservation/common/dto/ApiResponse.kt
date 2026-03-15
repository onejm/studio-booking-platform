package com.min.studioreservation.common.dto

import com.min.studioreservation.common.exception.ErrorType

data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val detail: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "요청이 성공했습니다."): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = "S001",
                message = message,
                data = data
            )
        }

        fun <T> error(
            errorType: ErrorType,
            detail: String? = null,
            data: T? = null
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = errorType.code,
                message = errorType.message,
                detail = detail,
                data = data
            )
        }
    }
}
