package com.example.demo.domain.dto.Raffle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RaffleResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadResultDTO{
        private Long raffle_id;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleDetailDTO{
        private List<String> imageUrls;
        private String name;
        private String category;
        private int ticketNum;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private String description;
        private int minTicket;
        private int view;
        private int likeCount;
        private int applyCount;
        private String nickname;
        private int followCount;
        private int reviewCount;
    }
}
