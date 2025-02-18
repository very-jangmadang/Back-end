package com.example.demo.domain.converter;

import com.example.demo.base.Constants;
import com.example.demo.domain.dto.Draw.DrawResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static java.time.LocalTime.from;
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

        Duration duration = Duration.between(now(), raffle.getEndAt().plusHours(Constants.DRAW_DEADLINE));

        return DrawResponseDTO.ResultDto.builder()
                .raffleId(raffle.getId())
                .minTicket(raffle.getMinTicket())
                .applyTicket(applyTicket)
                .totalAmount(BigDecimal.valueOf(applyTicket).multiply(new BigDecimal("93")))
                .remainedMinutes(duration.toMinutes())
                .build();
    }
}