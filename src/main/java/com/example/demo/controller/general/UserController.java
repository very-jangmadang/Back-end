package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @GetMapping("api/member/user-info")
    public void getUserInfo(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // username은 user_id입니다
            log.info("현재 사용자 ID: {}", username); // 로그에 사용자 ID 출력
            log.info("Authentication Object: {}", authentication);
        } else {
            log.warn("인증되지 않은 사용자"); // 인증되지 않은 사용자 로그
        }
    }

    @PostMapping("api/permit/logout")
    public ApiResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        log.info("브라우저의 쿠키 삭제 완료");

        return ApiResponse.of(SuccessStatus.USER_LOGOUT_SUCCESS, 1);
    }
}