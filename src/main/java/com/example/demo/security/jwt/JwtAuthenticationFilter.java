package com.example.demo.security.jwt;

import com.example.demo.base.code.ErrorReasonDTO;
import com.example.demo.base.status.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.io.PrintWriter;

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
        String accessToken = extractTokenFromCookies(request);

        if (accessToken == null) {
            sendJsonErrorResponse(response, ErrorStatus.TOKEN_NOT_FOUND);
            log.info("쿠키에 토큰없음");
            return;
        }

        // 엑세스 토큰 만료 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            sendJsonErrorResponse(response, ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN);
            log.info("엑세스 토큰 만료");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            sendJsonErrorResponse(response, ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN);
            log.info("엑세스 토큰이 아님");
            return;
        }

        // 토큰에서 인증정보 생성
        Authentication authentication = jwtUtil.getAuthentication(accessToken);
        log.info("Setting Authentication: {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Current Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }

    // 필터 통과
    private boolean isPermittedRequest(String requestURI) {
        return requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/api/permit/") ||
                requestURI.startsWith("/login") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.equals("/home") ||
                requestURI.equals("/nickname")||

                requestURI.startsWith("/payment/") || // yoon 임시
                requestURI.startsWith("/hello.html") || // yoon 임시
                requestURI.startsWith("/index.html") ; // yoon 임시
    }

    // 쿠키에서 토큰 추출
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // JSON 형식으로 응답
    private void sendJsonErrorResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
        ErrorReasonDTO errorReason = errorStatus.getReason();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorReason.getHttpStatus().value());

        String jsonResponse = String.format("{\"isSuccess\": %b, \"code\": \"%s\", \"message\": \"%s\"}",
                errorReason.isSuccess(),
                errorReason.getCode(),
                errorReason.getMessage());

        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}
