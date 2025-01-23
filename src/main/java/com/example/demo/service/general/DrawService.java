package com.example.demo.service.general;

import com.example.demo.domain.dto.DrawResponseDTO;

import java.util.Map;

public interface DrawService {

    Map<String, Object> getDrawRaffle(Long raffleId);

    DrawResponseDTO.WinnerDto getWinner(Long raffleId);

    DrawResponseDTO.RaffleResultDto getResult(Long raffleId);

    Long selfDraw(Long raffleId);
}
