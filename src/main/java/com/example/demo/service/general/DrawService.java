package com.example.demo.service.general;

import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface DrawService {

    Delivery draw(Raffle raffle, List<Apply> applyList);

    void cancel(Raffle raffle, List<Apply> applyList);

    DrawResponseDTO.RaffleResult getDrawRaffle(Long raffleId, Authentication authentication);

    DrawResponseDTO.ResultDto getResult(Long raffleId, Authentication authentication);

    String selfDraw(Long raffleId, Authentication authentication);

    DrawResponseDTO.CancelDto forceCancel(Long raffleId, Authentication authentication);

    String redraw(Long raffleId, Authentication authentication);
}
