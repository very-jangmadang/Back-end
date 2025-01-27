package com.example.demo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MypageResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleDto {
        private Long raffleId;
        private String raffleName;
        private String raffleImage;
        private int ticketNum;
        private int applyNum;
        private Long timeUntilEnd;
        private boolean finished;
        private boolean liked;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyListDto {
        List<RaffleDto> raffleList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private Long addressId;
        private String addressName;
        private String recipientName;
        private String addressDetail;
        private Boolean isDefault;
    }
  
}
