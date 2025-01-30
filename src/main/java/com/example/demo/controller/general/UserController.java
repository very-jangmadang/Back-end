package com.example.demo.controller.general;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
}