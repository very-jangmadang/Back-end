package com.example.demo.security.jwt;

import com.example.demo.base.code.ErrorReasonDTO;
import com.example.demo.base.code.exception.CustomException;
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

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("요청된 URI: {}", request.getRequestURI());
        log.info("요청된 메서드: {}", request.getMethod());
        log.info("Referer: {}", request.getHeader("Referer"));
        log.info("Origin: {}", request.getHeader("Origin"));

        // 게스트, 유저 둘다 이용가능한 uri
        if (isPermittedRequest(requestURI)) {
            log.info("있으면 저장, 없으면 통과 URI");
            String accessToken = extractTokenFromCookies(request);

            if (accessToken == null) {
                accessToken = extractTokenFromHeader(request);
            }

            if (accessToken != null) {
                if(jwtUtil.isExpired(accessToken)) {
                    sendJsonErrorResponse(response, ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN);
                    log.info("엑세스 토큰 만료");
                    return;
                }

                log.info("로그인 한 경우");
                Authentication authentication = jwtUtil.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("저장완료");
            }
            log.info("다음 필터 실행");
            filterChain.doFilter(request,response);
            return;
        }

        // 쿠키에서 토큰 가져오기
        String accessToken = extractTokenFromCookies(request);
        if (accessToken == null) {
            accessToken = extractTokenFromHeader(request);
        }

        if (accessToken == null) {
            sendJsonErrorResponse(response, ErrorStatus.TOKEN_NOT_FOUND);
            log.info("쿠키와 헤더에 토큰없음");
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

        // 토큰 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            sendJsonErrorResponse(response, ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN);
            log.info("엑세스 토큰이 아님");
            return;
        }

        // 토큰에서 인증정보 생성
        Authentication authentication = jwtUtil.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
                requestURI.equals("/favicon.ico");
    }

    // 쿠키에서 토큰 추출
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.info("쿠키 이름: {}, 값: {}", cookie.getName(), cookie.getValue());
                if ("access".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        } else {log.info("요청에 쿠키가 없음");}
        return null;
    }

    // 헤더에서 토큰 추출
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", header);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // JSON 형식으로 응답
    private void sendJsonErrorResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
        // CORS 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", "https://www.jangmadang.site");
        response.setHeader("Access-Control-Allow-Credentials", "true");

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
