package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Notification.NotificationResponseDTO;
import com.example.demo.service.general.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/member/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/host")
    public ApiResponse<List<NotificationResponseDTO>> getHostNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getHostNotifications();

        return ApiResponse.of(SuccessStatus._OK, notifications);
    }

    @GetMapping("/winner")
    public ApiResponse<List<NotificationResponseDTO>> getWinnerNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getWinnerNotifications();

        return ApiResponse.of(SuccessStatus._OK, notifications);
    }

    @GetMapping("/user")
    public ApiResponse<List<NotificationResponseDTO>> getUserNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications();

        return ApiResponse.of(SuccessStatus._OK, notifications);
    }

    @GetMapping("/applicants")
    public ApiResponse<List<NotificationResponseDTO>> getApplicantNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getApplicantNotifications();

        return ApiResponse.of(SuccessStatus._OK, notifications);
    }
}
