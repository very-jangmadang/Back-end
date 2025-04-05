package com.example.demo.entity.base.enums.Notification;

public enum NotificationEvent {

    //거래 알림
    //개최자용
    RAFFLE_ENDED(NotificationTargetType.RAFFLE),
    DELIVERY_ADDRESS_MISSING(NotificationTargetType.DELIVERY),
    DELIVERY_INVOICE_MISSING(NotificationTargetType.DELIVERY),

    //당첨자용
    RAFFLE_RESULT(NotificationTargetType.RAFFLE),
    DELIVERY_ADDRESS_REQUIRED(NotificationTargetType.DELIVERY),
    DELIVERY_DELAYED(NotificationTargetType.DELIVERY),
    REVIEW_REQUEST(NotificationTargetType.REVIEW),
    DELIVERY_ADDRESS_DUE(NotificationTargetType.DELIVERY),

    //시스템 알림
    TICKET_CHARGED(NotificationTargetType.PAYMENT),
    EXCHANGE_REQUIRED(NotificationTargetType.EXCHANGE),
    EXCHANGE_COMPLETED(NotificationTargetType.EXCHANGE);

    //마케팅 알림



    private final NotificationTargetType targetType;

    NotificationEvent(NotificationTargetType targetType) {
        this.targetType = targetType;
    }

    public NotificationTargetType getTargetType() {
        return targetType;
    }
}
