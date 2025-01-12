package com.example.demo.domain.dto;

import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RaffleResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadResultDTO{
//        private String imageUrl;
        private String title;
        private int ticketNum;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleDetailDTO{
//        private String imageUrl
        private String name;
        private Category category;
        private int ticketNum;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private String description;
        private int minTicket;
        private int view;
        private int likeCount;

        private String nickname;


    }
}
