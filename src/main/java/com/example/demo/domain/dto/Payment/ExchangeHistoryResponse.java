package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeHistoryResponse {

    private String exchangeId;
    private LocalDateTime exchangedDate; // 환전 일자
    private int quantity; // 환전 수량
    private String exchangeMethod; // 환전 수단
    private int amount; // 환전 금액

    public ExchangeHistoryResponse(Long exchangeId, LocalDateTime exchangedDate, int quantity, String exchangeMethod, int amount) {
        this.exchangeId = exchangeId.toString();
        this.exchangedDate = exchangedDate;
        this.quantity = quantity;
        this.exchangeMethod = exchangeMethod;
        this.amount = amount;
    }

}