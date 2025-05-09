package com.example.demo.domain.dto.Notification;

import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.Notification.NotificationEvent;
import lombok.Data;
import lombok.Getter;

@Data
public class NotificationRequestDTO {
        private NotificationEvent event;
        private String title;
        private String message;
        private String action;
        private User user;
}
