package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.security.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/permit")
@Slf4j
public class RefreshController {

    private final JWTUtil jwtUtil;

    @Operation(summary = "엑세스 토큰 재발급")
    @PostMapping("/refresh")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        log.info("엑세스 토큰 재발급 시작");
        String refresh = null;

        // 요청에서 쿠키 가져오기, 쿠키에서 리프레시 토큰 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("쿠키 없음");
            throw new CustomException(ErrorStatus.TOKEN_NOT_FOUND);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // 리프레시 토큰 여부
        if (refresh == null) {
            log.info("리프레시 토큰 없음");
            throw new CustomException(ErrorStatus.TOKEN_NOT_FOUND);
        }

        // 리프레시 토큰 만료 확인
        if (jwtUtil.isExpired(refresh)) {
            log.info("리프레시 토큰 만료");
            throw new CustomException(ErrorStatus.TOKEN_INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰 맞는지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            log.info("리프레시 토큰이 아님");
            throw new CustomException(ErrorStatus.TOKEN_INVALID_REFRESH_TOKEN);
        }

        // 정보 가져오기
        String userId = jwtUtil.getId(refresh);
        String email = jwtUtil.getEmail(refresh);

        // 토큰 발급
        String newAccessToken = jwtUtil.createAccessToken("access", Long.parseLong(userId), email);
        String newRefreshToken = jwtUtil.createRefreshToken("refresh", Long.parseLong(userId), email);

        // 응답
        response.addCookie(jwtUtil.createCookie("access", newAccessToken, 24 * 60 * 60)); // 24시간(개발용)
        response.addCookie(jwtUtil.createCookie("refresh", newRefreshToken, 7 * 24 * 60 * 60)); // 1주일(개발용)

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    @Operation(summary = "무제한 토큰 생성")
    @PostMapping("/access")
    public ApiResponse<?> develop(Long id, String email) {

        return ApiResponse.of(SuccessStatus._OK, jwtUtil.createToken("access", id, email));
    }
}
