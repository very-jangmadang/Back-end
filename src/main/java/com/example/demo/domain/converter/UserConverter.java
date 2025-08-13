package com.example.demo.domain.converter;

import com.example.demo.domain.dto.User.UserResponseDTO;
import com.example.demo.entity.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserConverter {

    public static User toUser(String nickname, String email, Boolean isBusiness, String businessCode) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .isBusiness(isBusiness)
                .businessCode(businessCode)
                .build();
    }

    public static UserResponseDTO.SignUpResponseDTO toSignUpResponseDTO(String nickname){
        return UserResponseDTO.SignUpResponseDTO.builder()
                .nickname(nickname)
                .build();
    }
}
