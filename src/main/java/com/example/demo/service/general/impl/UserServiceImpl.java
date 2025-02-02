package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.UserConverter;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.UserService;
import com.example.demo.service.handler.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean isExistUser(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Long findIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    @Override
    @Transactional
    public void createUser(String email) {

        // 1. 랜덤 닉네임 생성 후, 닉네임 중복 검사
        String nickname;

        do {
            nickname = NicknameGenerator.generateNickname();
        } while (userRepository.existsByNickname(nickname)); // 중복 확인

        // 2. 유저 등록
        User user = UserConverter.toUser(nickname, email);

        // 3. 저장
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addRefreshToken(Long userId, String token) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

       user.setRefreshToken(token);
    }
}
