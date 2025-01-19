package com.example.demo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApplyResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnterDto {
        private Long raffleId;
        private String raffleName;
        private String raffleImage;
        private int ticketNum;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyDto {
        private Long userId;
        private Long raffleId;
        private String raffleImage;
    }

}
