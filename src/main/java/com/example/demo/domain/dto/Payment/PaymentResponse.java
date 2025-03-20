package com.example.demo.domain.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private LocalDateTime purchaseDate; // 구매 일자
    private int user_ticket; // 현재 유저 티켓
    private String paymentMethod; // 결제 수단
    private int amount; // 결제 금액
}