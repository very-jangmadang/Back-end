package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRequest {
//    private int quantity; // 환전 수량
    private int amount; // 환전 티켓 수
}