package com.example.demo.service.general;

import com.example.demo.domain.dto.Notification.NotificationResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.util.List;


public interface NotificationService {
    void sendHostForEndedRaffle(Raffle raffle);
    List<NotificationResponseDTO> getHostNotifications();
    void sendHostForUnenteredAddress(Delivery delivery);
    void sendWinnerForUnenteredAddress(Delivery delivery);
    void sendHostForUnenteredInvoice(Delivery delivery);
    List<NotificationResponseDTO> getWinnerNotifications();
    void sendWinnerForEndedRaffle(User user, Raffle raffle);
    void sendWinnerForUnenteredInvoice(Delivery delivery);
    void sendWinnerForExtendedDeliveryDue(Delivery delivery);
    void sendWinnerForCancel(Raffle raffle);
    List<NotificationResponseDTO> getUserNotifications();
    void sendAllForOpen(Raffle raffle);
    List<NotificationResponseDTO> getApplicantNotifications();
    void sendApplicantForEnd(Raffle raffle);
}
