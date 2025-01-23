package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.domain.converter.DrawConverter.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplyRepository applyRepository;
    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public Map<String, Object> getDrawRaffle(Long raffleId) {

        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        RaffleStatus raffleStatus = raffle.getRaffleStatus();

        if (raffleStatus == RaffleStatus.UNOPENED || raffleStatus == RaffleStatus.ACTIVE)
            throw new CustomException(ErrorStatus.DRAW_YET);

        Map<String, Object> result = new HashMap<>();
        if (raffle.getUser().equals(user)) {
            if (raffleStatus == RaffleStatus.ENDED) {
                result.put("drawDto", null);
                result.put("deliveryId", raffle.getDelivery().getId());

                return result;
            } else {
                result.put("drawDto", null);
                result.put("deliveryId", null);
            }
        }

        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        List<String> nicknameList = applyList.stream()
                .map(apply -> apply.getUser().getNickname())
                .collect(Collectors.toList());

        result.put("drawDto", toDrawDto(raffle, nicknameList));
        result.put("deliveryId", null);
        return result;

    }

    @Override
    public DrawResponseDTO.WinnerDto getWinner(Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        return toWinnerDto(raffle);

    }

    @Override
    public DrawResponseDTO.RaffleResultDto getResult(Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        return toRaffleResultDto(raffle, applyNum * ticket);
    }

    @Override
    public Long selfDraw(Long raffleId) {

        return null;
    }
}
