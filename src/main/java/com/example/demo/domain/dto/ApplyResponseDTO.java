package com.example.demo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ApplyResponseDTO {

    @Getter
    @AllArgsConstructor
    public static class EnterDto {
        private String imageUrl;
        private String raffleName;
        private int ticketNum;
    }

    @Getter
    @AllArgsConstructor
    public static class ApplyDto {
        private String redirectUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class SuccessDto {
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class FailDto {
        private String raffleName;
        private int missingTicket;
    }
}
