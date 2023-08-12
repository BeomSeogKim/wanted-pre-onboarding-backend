package com.wanted.internship.exception;

import com.wanted.internship.dto.exception.CustomErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<CustomErrorResponse> handleException(IllegalArgumentException illegalArgumentException) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(CustomErrorResponse.of(illegalArgumentException));
    }
}
