package com.example.demo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@WebFilter("/*")
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 토큰 검증 없이 통과하는 URI
        if (isPermittedRequest(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 쿠키에서 토큰 가져오기
        String token = extractTokenFromCookies(request);

        // 2. 토큰 검증
        if (token != null) {

            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                // 토큰에서 인증정보 생성
                Authentication authentication = jwtUtil.getAuthentication(token);
                log.info("Setting Authentication: {}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Current Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러
                response.sendRedirect("/login"); // 로그인 페이지로 리다이렉트
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러
            response.sendRedirect("/login"); // 로그인 페이지로 리다이렉트
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermittedRequest(String requestURI) {
        return requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/api/permit/") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.equals("/login") ||
                requestURI.equals("/home") ||
                requestURI.equals("/nickname")||

                requestURI.startsWith("/payment") || // yoon 임시
                requestURI.startsWith("/hello.html") || // yoon 임시
                requestURI.startsWith("/index.html") ; // yoon 임시
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}