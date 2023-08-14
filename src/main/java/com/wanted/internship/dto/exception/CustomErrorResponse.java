package com.wanted.internship.dto.exception;

import com.wanted.internship.exception.NoAuthenticationException;
import com.wanted.internship.exception.NoAuthorityException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    public static CustomErrorResponse of(NoAuthenticationException noAuthenticationException) {
        return new CustomErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "NoAuthenticationException",
                noAuthenticationException.getMessage()
        );
    }

    public static CustomErrorResponse of(UsernameNotFoundException usernameNotFoundException) {
        return new CustomErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "usernameNotFoundException",
                usernameNotFoundException.getMessage()
        );
    }

    public static CustomErrorResponse of(NoAuthorityException noAuthorityException) {
        return new CustomErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "noAuthorityException",
                noAuthorityException.getMessage()
        );
    }

    public static CustomErrorResponse of(MethodArgumentNotValidException methodArgumentNotValidException) {
        return new CustomErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "methodArgumentNotValidException",
                methodArgumentNotValidException.getFieldError().getDefaultMessage()
        );
    }
}
