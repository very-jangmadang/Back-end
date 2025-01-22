package com.example.demo.domain.dto;

import com.example.demo.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class DrawResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawDto {
        private Long raffleId;
        private List<String> nicknameList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinnerDto {
        private Long raffleId;
        private Long winnerId;
        private String winnerNickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryDto {
        private Long raffleId;
        private Long winnerId;
        private List<AddressDto> addressList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String addressName;
        private String addressDetail;
        private Boolean isDefault;
    }
}
