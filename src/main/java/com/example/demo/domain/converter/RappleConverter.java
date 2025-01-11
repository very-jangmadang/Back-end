package com.example.demo.domain.converter;

import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.entity.Rapple;

import java.time.Duration;
import java.time.LocalDateTime;

public class RappleConverter {

    public static HomeResponseDTO.RappleDTO toRappleDTO(Rapple rapple){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = rapple.getEndAt();

        Duration duration = Duration.between(now, endAt);

        return HomeResponseDTO.RappleDTO.builder()
                .rappleId(rapple.getId())
                .like(false)
                .name(rapple.getName())
                .ticketNum(rapple.getTicketNum())
                .timeUntilEnd(duration)
                .participantNum(rapple.getApplyList().size())
                .finish(false)
                .build();
    }

}
