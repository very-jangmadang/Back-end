package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeResponse {
    private String message;

    public ExchangeResponse(String s) {
        this.message = s;
    }
}