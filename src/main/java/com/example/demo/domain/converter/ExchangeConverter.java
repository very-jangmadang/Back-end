package com.example.demo.domain.converter;

import com.example.demo.entity.Payment.Exchange;
import com.example.demo.domain.dto.Payment.ExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.ExchangeRequest;
import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExchangeConverter {

    public ExchangeHistoryResponse toExchangeHistoryResponse(Exchange exchange, User user) {
        return new ExchangeHistoryResponse(
                exchange.getId(),
                exchange.getExchangedAt(),
                user.getTicket_num(),
                "통장입금",
                exchange.getAmount()
        );
    }

    public Exchange toEntity(User user, ExchangeRequest request) {
        Exchange exchange = new Exchange();
        exchange.setUser(user);
        exchange.setAmount(request.getAmount());
        exchange.setQuantity(request.getQuantity());
        exchange.setExchangeMethod("통장 입금");
        exchange.setExchangedAt(LocalDateTime.now());
        return exchange;
    }

}