package com.example.demo.domain.converter;


import com.example.demo.domain.dto.Payment.PaymentResponse;
import com.example.demo.domain.dto.TopUp.TopUpResponse;
import com.example.demo.entity.Payment.TopUp;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TopUpConverter {

    public TopUpResponse toTopUpResponse(TopUp topUp, User user) {
        return new TopUpResponse(
                topUp.getId(),
                topUp.getConfirmedAt(),
                user.getTicket_num(),
                "VERY",
                topUp.getTickets()
        );
    }
}
