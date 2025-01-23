package com.example.demo.domain.converter;

import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Address;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.math.BigDecimal;
import java.util.List;

public class DrawConverter {

    public static DrawResponseDTO.DrawDto toDrawDto(Raffle raffle, List<String> nicknameList) {
        return DrawResponseDTO.DrawDto.builder()
                .raffleId(raffle.getId())
                .nicknameList(nicknameList)
                .build();
    }

    public static DrawResponseDTO.WinnerDto toWinnerDto(Raffle raffle) {
        return DrawResponseDTO.WinnerDto.builder()
                .raffleId(raffle.getId())
                .winnerId(raffle.getWinner().getId())
                .winnerNickname(raffle.getWinner().getNickname())
                .build();
    }

    public static DrawResponseDTO.RaffleResultDto toRaffleResultDto(Raffle raffle, int applyTicket) {
        return DrawResponseDTO.RaffleResultDto.builder()
                .raffleId(raffle.getId())
                .minTicket(raffle.getMinTicket())
                .applyTicket(applyTicket)
                .totalAmount(BigDecimal.valueOf(applyTicket).multiply(new BigDecimal("0.93")))
                .build();
    }
}