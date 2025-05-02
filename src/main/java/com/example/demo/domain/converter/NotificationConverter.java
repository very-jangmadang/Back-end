package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Inquiry.InquiryRequestDTO;
import com.example.demo.domain.dto.Notification.NotificationRequestDTO;
import com.example.demo.domain.dto.Notification.NotificationResponseDTO;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.Notification.NotificationType;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class NotificationConverter {

    public static Notification toNotification(NotificationRequestDTO request) {
        return Notification.builder()
                .user(request.getUser())
                .title(request.getTitle())
                .content(request.getMessage())  // content = message
                .action(request.getAction())
                .event(request.getEvent())
                .type(request.getEvent().getNotificationType())
                .targetType(request.getEvent().getTargetType())
                .build();
    }

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .type(notification.getType().toString().toLowerCase())
                .targetType(notification.getTargetType().toString().toLowerCase())
                .role(notification.getEvent().getRole())
                .event(notification.getEvent().toString().toLowerCase())
                .title(notification.getTitle())
                .message(notification.getContent())
                .action(notification.getAction())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
