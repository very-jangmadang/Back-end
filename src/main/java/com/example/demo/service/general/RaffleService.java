package com.example.demo.service.general;

import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;

public interface RaffleService {

    // 래플 업로드
    RaffleResponseDTO.UploadResultDTO uploadRaffle(RaffleRequestDTO.UploadDTO request);

    // 래플 상세조회
    RaffleResponseDTO.RaffleDetailDTO getRaffleDetailsDTO(Long id);
}
