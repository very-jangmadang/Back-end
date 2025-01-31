package com.example.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomLoginEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // 이미 로그인된 사용자라면 로그인 페이지를 그대로 보여줌
            request.getRequestDispatcher("http://localhost:8080/login/oauth2/code/kakao").forward(request, response);
        } else {
            // 로그인되지 않은 경우 기본 동작 수행
            response.sendRedirect("http://localhost:8080/login/oauth2/code/kakao");
        }
    }
}