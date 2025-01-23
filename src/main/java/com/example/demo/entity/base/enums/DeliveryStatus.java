package com.example.demo.entity.base.enums;

public enum DeliveryStatus {
    ADDRESS_WAITING,       // 배송지 입력 대기
    READY,          // 배송지 입력 및 결제 완료
    SHIPPED,        // 운송장 입력 완료 (발송 완료)
    CANCELLED       // 당첨 취소
}
