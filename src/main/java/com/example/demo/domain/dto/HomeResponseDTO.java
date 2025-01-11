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

    private List<RappleDTO> approaching;
    private List<RappleDTO> myLikeRapples;
    private List<RappleDTO> rapples;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RappleDTO {
        private Long rappleId;
        private String name;
        private int ticketNum;
        private Duration timeUntilEnd;
        private boolean finish;
        private int participantNum;
        private boolean like;
    }
}
