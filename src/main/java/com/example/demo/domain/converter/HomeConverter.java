package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Home.HomeRaffleDTO;
import com.example.demo.entity.Raffle;

import java.time.Duration;
import java.time.LocalDateTime;


public class HomeConverter {

    public static HomeRaffleDTO toHomeRaffleDTO(Raffle raffle, boolean likeStatus){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = raffle.getEndAt();

        Duration duration = Duration.between(now, endAt);
        boolean finish = duration.isNegative();


        return HomeRaffleDTO.builder()
                .raffleId(raffle.getId())
                .imageUrl(raffle.getImageUrl())
                .like(likeStatus)
                .name(raffle.getName())
                .ticketNum(raffle.getTicketNum())
                .timeUntilEnd(duration.toSeconds())
                .participantNum(raffle.getApplyList().size())
                .finish(finish)
                .build();
    }

}
