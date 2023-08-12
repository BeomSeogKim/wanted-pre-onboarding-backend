package com.wanted.internship.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.internship.dto.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerException implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        // todo DTO 만들기
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                CustomErrorResponse.accessDeniedError()
        ));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
