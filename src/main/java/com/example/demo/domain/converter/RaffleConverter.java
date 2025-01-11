package com.example.demo.domain.converter;

import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.entity.Raffle;
import java.time.Duration;
import java.time.LocalDateTime;

public class RaffleConverter {

    public static HomeResponseDTO.RaffleDTO toRaffleDTO(Raffle raffle){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = raffle.getEndAt();

        Duration duration = Duration.between(now, endAt);

        return HomeResponseDTO.RaffleDTO.builder()
                .raffleId(raffle.getId())
                .like(false)
                .name(raffle.getName())
                .ticketNum(raffle.getTicketNum())
                .timeUntilEnd(duration)
                .participantNum(raffle.getApplyList().size())
                .finish(false)
                .build();
    }

}
