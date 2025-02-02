package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.security.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member")
public class ReissueController {

    private final JWTUtil jwtUtil;

    @Operation(summary = "엑세스 토큰 재발급")
    @PostMapping("/reissue")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;

        // 요청에서 쿠키 가져오기, 쿠키에서 리프레시 토큰 가져오기
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // 리프레시 토큰 없으면 에러 반환
        if (refresh == null) {
            return ApiResponse.onFailure(ErrorStatus.TOKEN_NOT_FOUND, null);
        }

        // 리프레시 토큰 만료됐는지 확인
        if (jwtUtil.isExpired(refresh)) {
            return ApiResponse.onFailure(ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN, null);
        }

        // 리프레시 토큰 맞는지 확인
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return ApiResponse.onFailure(ErrorStatus.TOKEN_INVALID_ACCESS_TOKEN, null);
        }

        // 엑세스 토큰 발급
        String userId = jwtUtil.getId(refresh);
        String email = jwtUtil.getEmail(refresh);

        String newAccessToken = jwtUtil.createAccessToken("access", Long.parseLong(userId), email);

        // 응답 완료
        response.setHeader("access", newAccessToken);

        return ApiResponse.of(SuccessStatus._OK, null);
    }
}
