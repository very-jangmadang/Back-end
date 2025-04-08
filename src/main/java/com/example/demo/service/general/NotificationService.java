package com.example.demo.service.general;

import com.example.demo.domain.dto.Notification.NotificationResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

import java.util.List;


public interface NotificationService {
    void sendHostForEndedRaffle(Raffle raffle);
    List<NotificationResponseDTO> getHostNotifications();
    void sendHostForUnenteredAddress(Delivery delivery);
    void sendHostForUnenteredInvoice(Delivery delivery);
    List<NotificationResponseDTO> getWinnerNotifications();
    void sendWinnerForEndedRaffle(Raffle raffle);
}
