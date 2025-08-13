package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.UserConverter;
import com.example.demo.domain.dto.User.UserRequestDTO;
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
import org.springframework.util.StringUtils;

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
    public void createUser(String nickname, String email, Boolean isBusiness, String businessCode) {
        // 1. 닉네임 중복 검사
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorStatus.USER_NICKNAME_ALREADY_EXISTS);
        }
        // 2. 유저 등록
        User user = UserConverter.toUser(nickname, email, isBusiness, businessCode);

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

        User user = userRepository.findById(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        log.info("유저 닉네임: {}", user.getNickname());
        return "user";
    }

    @Override
    public void registerBusiness(HttpSession session, UserRequestDTO.businessCodeDTO request){

        if (session == null) {
            throw new CustomException(ErrorStatus.USER_WITHOUT_SESSION);
        }

        boolean isBusiness = Boolean.TRUE.equals(request.getIsBusiness());
        session.setAttribute("is_business", isBusiness);



        if (isBusiness) {
            if (!StringUtils.hasText(request.getBusinessCode())) {
                throw new CustomException(ErrorStatus.BUSINESS_CODE_REQUIRED);
            }
            session.setAttribute("business_code", request.getBusinessCode());
        } else {
            // 일반 사용자면 혹시 이전 단계에서 들어간 값 정리
            session.removeAttribute("business_code");
        }
    }
}
