package com.example.demo.domain.converter;

import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;

public class ApplyConverter {

    public static ApplyResponseDTO.EnterDto toEnterDto(Raffle raffle) {
        return ApplyResponseDTO.EnterDto.builder()
                .raffleId(raffle.getId())
                .raffleName(raffle.getName())
                .raffleImage(raffle.getImageUrl())
                .ticketNum(raffle.getTicketNum())
                .build();
    }

    public static ApplyResponseDTO.ApplyDto toApplyDto(Apply apply) {
        return ApplyResponseDTO.ApplyDto.builder()
                .userId(apply.getUser().getId())
                .raffleId(apply.getRaffle().getId())
                .raffleImage(apply.getRaffle().getImageUrl())
                .build();
    }

}
