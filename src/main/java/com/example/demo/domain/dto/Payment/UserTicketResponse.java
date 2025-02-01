package com.example.demo.domain.dto.Payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserTicketResponse {
    private int ticket;
    private LocalDateTime updatedAt;
}