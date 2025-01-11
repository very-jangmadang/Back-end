package com.example.demo.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDTO {

    private List<RaffleDTO> approaching;
    private List<RaffleDTO> myLikeRaffles;
    private List<RaffleDTO> raffles;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleDTO {
        private Long raffleId;
        private String name;
        private int ticketNum;
        private Duration timeUntilEnd;
        private boolean finish;
        private int participantNum;
        private boolean like;
    }
}
