package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.ApplyResponseDTO;

public interface ApplyService {

    ApplyResponseDTO.EnterDto getEnterRaffle(Long raffleId);

    ApplyResponseDTO.ApplyDto applyRaffle(Long userId, Long raffleId);

    ApplyResponseDTO.SuccessDto successApply(Long userId, Long raffleId);

    ApplyResponseDTO.FailDto failApply(Long userId, Long raffleId);
}
