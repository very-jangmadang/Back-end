package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.UserConverter;
import com.example.demo.domain.dto.User.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.service.general.UserService;
import com.example.demo.service.handler.NicknameGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public boolean isExistUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Long findIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    @Override
    @Transactional
    public void createUser(String nickname, String email) {
        // 1. 닉네임 중복 검사
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorStatus.USER_NICKNAME_ALREADY_EXISTS);
        }
        // 2. 유저 등록
        User user = UserConverter.toUser(nickname, email);
        // 3. 유저 저장
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addRefreshToken(Long userId, String token) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

       user.setRefreshToken(token);
    }

    @Override
    @Transactional
    public UserResponseDTO.SignUpResponseDTO randomNickname() {

        String nickname;
        do {
            nickname = NicknameGenerator.generateNickname();
        } while (userRepository.existsByNickname(nickname)); // 중복 확인

        return UserConverter.toSignUpResponseDTO(nickname);
    }

    @Override
    public String isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            log.info("비회원");
            return "guest";
        }
        log.info("사용자 아이디: {}", authentication.getName());
        log.info("유저");
        return "user";
    }
}
