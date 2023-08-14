package com.wanted.internship.exception;

import com.wanted.internship.dto.exception.CustomErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<CustomErrorResponse> handleException(IllegalArgumentException illegalArgumentException) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(CustomErrorResponse.of(illegalArgumentException));
    }

    @ExceptionHandler(NoAuthenticationException.class)
    protected ResponseEntity<CustomErrorResponse> handleException(NoAuthenticationException noAuthenticationException) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(CustomErrorResponse.of(noAuthenticationException));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<CustomErrorResponse> handleException(UsernameNotFoundException usernameNotFoundException) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(CustomErrorResponse.of(usernameNotFoundException));
    }

    @ExceptionHandler(NoAuthorityException.class)
    protected ResponseEntity<CustomErrorResponse> handleException(NoAuthorityException noAuthorityException) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(CustomErrorResponse.of(noAuthorityException));
    }

}
