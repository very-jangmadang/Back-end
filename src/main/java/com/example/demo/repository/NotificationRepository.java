package com.example.demo.repository;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.Notification.NotificationEvent;
import com.example.demo.entity.base.enums.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserAndType(User user, NotificationType type);

    List<Notification> findByUserAndEventInAndCreatedAtAfter(User user, List<NotificationEvent> events, LocalDateTime twoWeeksAgo);

    boolean existsByDelivery_IdAndEvent(Long deliveryId, NotificationEvent event);
}
