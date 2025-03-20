package com.example.demo.domain.dto.Inquiry;


import com.example.demo.entity.base.enums.InquiryStatus;
import jakarta.persistence.Column;
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
        private String nickname;
        private String title;
        private String content;
        private InquiryStatus status;
        @Column(name = "created_at")
        private LocalDateTime createdAt;

    }


