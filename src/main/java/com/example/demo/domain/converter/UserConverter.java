package com.example.demo.domain.converter;

import com.example.demo.entity.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserConverter {

    public static User toUser(String nickname, String email) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .build();
    }
}
