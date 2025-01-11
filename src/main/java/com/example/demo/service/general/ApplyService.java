package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.ApplyResponseDTO;

public interface ApplyService {

    ApiResponse<ApplyResponseDTO.EnterDto> getEnterRaffle(Long raffleId);

    ApiResponse<ApplyResponseDTO.ApplyDto> applyRaffle(Long userId, Long raffleId);

    ApiResponse<ApplyResponseDTO.SuccessDto> successApply(Long userId, Long raffleId);

    ApiResponse<ApplyResponseDTO.FailDto> failApply(Long userId, Long raffleId);
}
