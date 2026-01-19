package com.example.S2S.config;

import com.example.S2S.common.Enum.ErrorCode;
import com.example.S2S.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .resultCode(errorCode.getCode())
                .resultDesc(errorCode.getMessage())
                .responseTime(LocalDateTime.now().toString())
                .build();
        ObjectMapper objectMapper =
                new ObjectMapper();
        response.getWriter()
                .write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
