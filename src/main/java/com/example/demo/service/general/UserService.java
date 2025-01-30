package com.example.demo.service.general;

import com.example.demo.entity.User;

public interface UserService{

    // 로그인 한 회원이 신규 회원인지 확인 (나중에 한 사람이, 카카오,네이버로 만들면 어떻게 검증 해야할까)
    boolean isExistUser(String email);

    // 이메일로 유저 아이디 찾기
    Long findIdByEmail(String email);

    // 신규 회원 등록
    void createUser(String email);
}
