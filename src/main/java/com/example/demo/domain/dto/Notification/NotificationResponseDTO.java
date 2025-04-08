package com.example.demo.domain.dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

        private String type;
        private String targetType;
        private String role;
        private String event;
        private String title;
        private String message;
        private String action;
        private LocalDateTime createdAt;
}
