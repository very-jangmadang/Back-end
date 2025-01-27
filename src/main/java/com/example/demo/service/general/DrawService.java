package com.example.demo.service.general;

import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

import java.util.List;
import java.util.Map;

public interface DrawService {

    Delivery draw(Raffle raffle, List<Apply> applyList);

    void cancel(Raffle raffle, List<Apply> applyList);

    Map<String, Object> getDrawRaffle(Long raffleId);

    DrawResponseDTO.WinnerDto getWinner(Long raffleId);

    DrawResponseDTO.RaffleResultDto getRaffleResult(Long raffleId);

    String selfDraw(Long raffleId);

    DrawResponseDTO.CancelDto forceCancel(Long raffleId);
}
