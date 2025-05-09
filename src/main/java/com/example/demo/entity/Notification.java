package com.example.demo.entity;

import com.example.demo.entity.base.enums.Notification.NotificationEvent;
import com.example.demo.entity.base.enums.Notification.NotificationTargetType;
import com.example.demo.entity.base.enums.Notification.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private String action;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationTargetType targetType; // RAFFLE, DELIVERY, REVIEW, ...

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationRead> readStatuses = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

}
