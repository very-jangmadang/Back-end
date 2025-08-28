package com.example.demo.domain.dto.Raffle;

import com.example.demo.entity.base.enums.RaffleStatus;
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
    public static class ResponseDTO {
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
        private int minUser;
        private int minTicket;
        private int view;
        private int likeCount;
        private int applyCount;
        private String nickname;
        private Long storeId;
        private int followCount;
        private int reviewCount;
        private String userStatus;
        private String isWinner;
        private RaffleStatus raffleStatus;
        private Long deliveryId;
        private boolean followStatus;
        private boolean likeStatus;
        private String storeImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyDTO{
        private Long userId;
        private Long raffleId;
        private String raffleImage;
        private LocalDateTime endAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedApplyDTO{
        private int missingTickets;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyResultDTO{
        private ApplyDTO applyDTO;
        private FailedApplyDTO failedApplyDTO;
    }
}
