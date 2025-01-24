package com.example.demo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyResponseDTO {

    private Long userId;
    private Long raffleId;
    private String raffleImage;
    private LocalDateTime endAt;

}
