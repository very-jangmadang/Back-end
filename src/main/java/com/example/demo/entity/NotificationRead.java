package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationRead extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    // notification:readStatus=1:N
    @ManyToOne(fetch = FetchType.LAZY)
    private Notification notification;

    //user:readStatus=1:N
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private boolean isRead;
}
