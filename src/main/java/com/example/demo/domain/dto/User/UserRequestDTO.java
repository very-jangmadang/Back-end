package com.example.demo.domain.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


public class UserRequestDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class signUpDTO {
       private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class nicknameDTO{

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2~10 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 2~10자의 한글,숫자,영어만 사용 가능합니다.")
        private String nickname;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class businessCodeDTO{
        @NotBlank(message = "사업자 번호는 필수 입력값입니다.")
        @Size(min = 5, max = 20, message = "사업자번호는 최소 5자, 최대 20자여야 합니다")
        @Pattern(regexp = "^[0-9]{5,20}$", message = "사업자번호는 5~20자의 숫자만 사용 가능합니다.")
        private String businessCode;

        @NotNull
        private Boolean isBusiness;
    }

}
