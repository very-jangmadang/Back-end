package com.example.demo.service.general;

import com.example.demo.domain.dto.User.UserRequestDTO;
import com.example.demo.domain.dto.User.UserResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    // 로그인 한 회원이 신규 회원인지 확인 (나중에 한 사람이, 카카오,네이버로 만들면 어떻게 검증 해야할까)
    boolean isExistUserByEmail(String email);

    // 이메일로 유저 아이디 찾기
    Long findIdByEmail(String email);

    // 신규 회원 등록
    void createUser(String nickname, String email, Boolean isBusiness, String businessCode);

    // 리프레시 토큰 저장
    void addRefreshToken(Long userId, String token);

    UserResponseDTO.SignUpResponseDTO randomNickname();

    // 로그인 정보 확인
    String isLogin();

    // 사업자 등록
    void registerBusiness(HttpSession session, UserRequestDTO.businessCodeDTO request);
}
