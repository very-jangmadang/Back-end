package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String getCurrentUserId() {

        String username = "guest";
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
            logger.info("현재 사용자 ID: {}", username); // 로그에 사용자 ID 출력

            return username;

        } else {
            logger.warn("인증되지 않은 사용자"); // 인증되지 않은 사용자 로그
            return username;
        }
    }
}