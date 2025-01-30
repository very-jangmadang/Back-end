package com.example.demo.domain.converter;

import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

import java.math.BigDecimal;
import java.util.Set;

public class DrawConverter {

    public static DrawResponseDTO.DrawDto toDrawDto(
            Delivery delivery, Set<String> nicknameSet, boolean isWin) {

        return DrawResponseDTO.DrawDto.builder()
                .raffleId(delivery.getRaffle().getId())
                .nicknameSet(nicknameSet)
                .winnerId(delivery.getWinner().getId())
                .winnerNickname(delivery.getWinner().getNickname())
                .isWin(isWin)
                .deliveryId(delivery.getId())
                .build();
    }

    public static DrawResponseDTO.ResultDto toResultDto(Raffle raffle, int applyTicket) {
        return DrawResponseDTO.ResultDto.builder()
                .raffleId(raffle.getId())
                .minTicket(raffle.getMinTicket())
                .applyTicket(applyTicket)
                .totalAmount(BigDecimal.valueOf(applyTicket).multiply(new BigDecimal("93")))
                .build();
    }
}