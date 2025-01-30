package com.example.demo.domain.dto.User;

import lombok.*;


public class UserRequestDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class signUpDTO {

        String email;
    }

}
