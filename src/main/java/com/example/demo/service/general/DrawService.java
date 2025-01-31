package com.example.demo.service.general;

import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;

import java.util.List;

public interface DrawService {

    Delivery draw(Raffle raffle, List<Apply> applyList);

    void cancel(Raffle raffle, List<Apply> applyList);

    DrawResponseDTO.RaffleResult getDrawRaffle(Long raffleId);

    DrawResponseDTO.ResultDto getResult(Long raffleId);

    String selfDraw(Long raffleId);

    DrawResponseDTO.CancelDto forceCancel(Long raffleId);

    String redraw(Long raffleId);
}
