package com.example.demo.domain.dto.Inquiry;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class InquiryResponseDTO {

        private Long inquiryId;
        private Long userId;
        private Long raffleId;
        private String title;
        private String content;
        private LocalDateTime timestamp;

    }


