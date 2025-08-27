package com.example.demo.domain.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeHistoryResponse {

    private Long exchangeId;
    private LocalDateTime exchangedDate; // 환전 일자
    private int user_ticket; // 현재 유저 티켓
    private String exchangeMethod; // 환전 수단
    private int amount; // 환전 티켓 수량
}