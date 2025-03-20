package com.example.demo.domain.dto.Draw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

public class DrawResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrawDto {
        private Long raffleId;
        private Set<String> nicknameSet;
        private Long winnerId;
        private String winnerNickname;
        private boolean isWin;
        private Long deliveryId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDto {
        private Long raffleId;
        private int minTicket;
        private int applyTicket;
        private BigDecimal totalAmount;
        private long remainedMinutes;
    }

}
