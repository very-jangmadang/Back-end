package com.example.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://www.jangmadang.site");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // JSON 응답을 위한 Map 생성∑
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", "LOGIN4001");
        responseData.put("message", "인증되지 않은 사용자 접근입니다.");
        responseData.put("isSuccess", "false");

        // 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        log.info("인증되지않은 사용자 접근");

        // JSON 응답 변환 및 출력
        new ObjectMapper().writeValue(response.getWriter(), responseData);
    }
}