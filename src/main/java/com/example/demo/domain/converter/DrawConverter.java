package com.example.demo.domain.converter;

import com.example.demo.base.Constants;
import com.example.demo.domain.dto.Draw.DrawResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;

import static java.time.LocalTime.now;

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

        BigDecimal feeRate = new BigDecimal(Constants.FEE_RATE).multiply(new BigDecimal("100"));
        Duration duration = Duration.between(now(), raffle.getEndAt().plusHours(Constants.CHOICE_PERIOD));

        return DrawResponseDTO.ResultDto.builder()
                .raffleId(raffle.getId())
                .minTicket(raffle.getMinTicket())
                .applyTicket(applyTicket)
                .totalAmount(BigDecimal.valueOf(applyTicket).multiply(feeRate))
                .remainedMinutes(duration.toMinutes())
                .build();
    }
}