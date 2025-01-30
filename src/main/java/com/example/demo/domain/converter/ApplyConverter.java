package com.example.demo.domain.converter;

import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;

public class ApplyConverter {

    public static ApplyResponseDTO toApplyDto(Apply apply) {
        return ApplyResponseDTO.builder()
                .userId(apply.getUser().getId())
                .raffleId(apply.getRaffle().getId())
                .raffleImage(apply.getRaffle().getImages().get(0).getImageUrl())
                .endAt(apply.getRaffle().getEndAt())
                .build();
    }

}
