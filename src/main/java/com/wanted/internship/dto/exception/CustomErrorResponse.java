package com.wanted.internship.dto.exception;

import org.springframework.http.HttpStatus;

public record CustomErrorResponse(
        int status,
        String code,
        String message
) {
    public static CustomErrorResponse of(IllegalArgumentException illegalArgumentException) {
        return new CustomErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "IllegalArgumentException",
                illegalArgumentException.getMessage()
        );
    }

    public static CustomErrorResponse accessDeniedError() {
        return new CustomErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "ACCESS DENIED, 접근거부: 로그인이 필요합니다!"
        );
    }

    public static CustomErrorResponse authenticationError() {
        return new CustomErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION FAILED",
                "인증실패: 로그인이 필요합니다!"
        );
    }
}
