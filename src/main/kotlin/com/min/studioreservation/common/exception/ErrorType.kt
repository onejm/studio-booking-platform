package com.min.studioreservation.common.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INVALID_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "C004", "정렬 가능한 필드가 아닙니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 오류가 발생했습니다."),

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A005", "이메일 또는 비밀번호가 올바르지 않습니다."),
    ACCOUNT_WITHDRAWN(HttpStatus.FORBIDDEN, "A007", "탈퇴한 계정입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증 정보가 유효하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 존재하는 이메일입니다."),
    ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "U010", "이미 탈퇴한 회원입니다.")
}
