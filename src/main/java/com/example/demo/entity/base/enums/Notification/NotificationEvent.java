package com.example.demo.entity.base.enums.Notification;

public enum NotificationEvent {

    // 거래 알림 - 모두
    RAFFLE_OPENED(NotificationTargetType.RAFFLE, NotificationType.TRADE, "user"),

    //거래 알림 - 응모자
    RAFFLE_FINISHED(NotificationTargetType.RAFFLE, NotificationType.TRADE, "applicant"),

    // 거래 알림 - 개최자
    RAFFLE_ENDED(NotificationTargetType.RAFFLE, NotificationType.TRADE, "host"),
    DELIVERY_ADDRESS_MISSING(NotificationTargetType.DELIVERY, NotificationType.TRADE, "host"),
    DELIVERY_INVOICE_MISSING(NotificationTargetType.DELIVERY, NotificationType.TRADE, "host"),

    // 거래 알림 - 당첨자
    RAFFLE_RESULT(NotificationTargetType.RAFFLE, NotificationType.TRADE, "winner"),
    DELIVERY_ADDRESS_REQUIRED(NotificationTargetType.DELIVERY, NotificationType.TRADE, "winner"),
    DELIVERY_DELAYED(NotificationTargetType.DELIVERY, NotificationType.TRADE, "winner"),
    REVIEW_REQUEST(NotificationTargetType.REVIEW, NotificationType.TRADE, "winner"),
    DELIVERY_ADDRESS_DUE(NotificationTargetType.DELIVERY, NotificationType.TRADE, "winner"),
    INVOICE_DUE_OVER(NotificationTargetType.DELIVERY, NotificationType.TRADE, "winner"),
    DELIVERY_ADDRESS_CHECK(NotificationTargetType.DELIVERY, NotificationType.TRADE, "winner"),
    DELIVERY_DUE_EXTENDED(NotificationTargetType.DELIVERY, NotificationType.TRADE, "winner"),
    DELIVERY_DUE_CANCELLED(NotificationTargetType.RAFFLE, NotificationType.TRADE, "winner"),

    // 시스템 알림 - 유저
    TICKET_CHARGED(NotificationTargetType.PAYMENT, NotificationType.SYSTEM, "user"),
    EXCHANGE_REQUIRED(NotificationTargetType.EXCHANGE, NotificationType.SYSTEM, "user"),
    EXCHANGE_COMPLETED(NotificationTargetType.EXCHANGE, NotificationType.SYSTEM, "user"),

    // 마케팅 알림 (예: 광고성)
    //MARKETING_COUPON(NotificationTargetType.MARKETING, NotificationType.MARKETING, "user");

    ;

    private final NotificationTargetType targetType;
    private final NotificationType notificationType;
    private final String role;

    NotificationEvent(NotificationTargetType targetType, NotificationType notificationType, String role) {
        this.targetType = targetType;
        this.notificationType = notificationType;
        this.role = role;
    }

    public NotificationTargetType getTargetType() {
        return targetType;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getRole() {
        return role;
    }
}
