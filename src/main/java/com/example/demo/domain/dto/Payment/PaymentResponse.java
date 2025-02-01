package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private String paymentId;
    private LocalDateTime purchaseDate; // 구매 일자
    private int quantity; // 구매 수량
    private String paymentMethod; // 결제 수단
    private int amount; // 결제 금액

    public PaymentResponse(Long paymentId, LocalDateTime purchaseDate, int quantity, String paymentMethod, int amount) {
        this.paymentId = String.valueOf(paymentId);
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

}