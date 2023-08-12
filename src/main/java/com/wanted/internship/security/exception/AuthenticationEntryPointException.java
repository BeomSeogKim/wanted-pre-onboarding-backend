package com.wanted.internship.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.internship.dto.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntryPointException implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                CustomErrorResponse.authenticationError()
        ));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
