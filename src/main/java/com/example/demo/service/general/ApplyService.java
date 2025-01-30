package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.ApplyResponseDTO;

public interface ApplyService {

    ApplyResponseDTO applyRaffle(Long raffleId);

}
