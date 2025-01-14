package com.example.demo.service.general;

import com.example.demo.domain.dto.RaffleRequestDTO;
import com.example.demo.domain.dto.RaffleResponseDTO;
import com.example.demo.entity.User;

public interface RaffleService {

    // 사용자가 래플 업로드
    RaffleResponseDTO.UploadResultDTO uploadRaffle(RaffleRequestDTO.UploadDTO request);

    // 래플 상세조회
    RaffleResponseDTO.RaffleDetailDTO getRaffleDetailsDTO(Long id);
}
