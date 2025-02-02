package com.example.demo.controller;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("인증되지 않은 사용자");
            return "guest";
        }

        try {
            Long userId = Long.valueOf(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            String userEmail = user.getEmail();
            logger.info("현재 사용자 이메일: {}", userEmail);
            return userEmail;
        } catch (NumberFormatException e) {
            logger.error("유효하지 않은 사용자 ID: {}", authentication.getName(), e);
            throw new CustomException(ErrorStatus.TOKEN_MISSING);
        }
    }
}