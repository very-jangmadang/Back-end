package com.example.demo.domain.dto.Mypage;

import lombok.Getter;

public class MypageRequestDTO {

    @Getter
    public static class AddressDto {
        private Long addressId;
    }

    @Getter
    public static class AddressAddDto {
        private String addressName;
        private String recipientName;
        private String addressDetail;
        private String phoneNumber;
        private String message;
        private Boolean isDefault;
    }
}
