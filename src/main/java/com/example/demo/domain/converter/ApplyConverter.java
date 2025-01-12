package com.example.demo.domain.converter;

import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.entity.Raffle;

public class ApplyConverter {

    public static ApplyResponseDTO.EnterDto toEnterDto(Raffle raffle) {
        return ApplyResponseDTO.EnterDto.builder()
                .imageUrl(raffle.getImageUrl())
                .raffleName(raffle.getName())
                .ticketNum(raffle.getTicketNum())
                .build();
    }

    public static ApplyResponseDTO.SuccessDto toSuccessDto(Raffle raffle) {
        return ApplyResponseDTO.SuccessDto.builder()
                .imageUrl(raffle.getImageUrl())
                .build();
    }

    public static ApplyResponseDTO.FailDto toFailDto(Raffle raffle, int missingTicket) {
        return ApplyResponseDTO.FailDto.builder()
                .raffleName(raffle.getName())
                .missingTicket(missingTicket)
                .build();
    }

}
