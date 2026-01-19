package com.example.S2S.common;

import com.example.S2S.anotation.ApiMessage;
import com.example.S2S.dto.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class FormatResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        ApiResponse<Object> res = new ApiResponse<Object>();
        res.setResultCode(Integer.parseInt(String.valueOf(status)));

        if (body instanceof String || body instanceof Resource || body instanceof byte[]) {
            return body;
        }
        if (status >= 400) {
            return body;
        } else {
            res.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setResultDesc(message != null ? message.value() : "CALL API SUCCESS");
            res.setResponseTime(String.valueOf(LocalDateTime.now()));
        }

        return res;
    }
}
