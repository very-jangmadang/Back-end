package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.demo.domain.converter.DeliveryConverter.toDelivery;
import static com.example.demo.domain.converter.DrawConverter.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplyRepository applyRepository;
    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Delivery draw(Raffle raffle, List<Apply> applyList) {

        Random random = new Random();
        int randomIndex = random.nextInt(applyList.size());

        User winner = applyList.get(randomIndex).getUser();
        raffle.setWinner(winner);
        raffleRepository.save(raffle);

        Delivery delivery = toDelivery(raffle);
        deliveryRepository.save(delivery);

        return delivery;

    }

    @Override
    @Transactional
    public void cancel(Raffle raffle, List<Apply> applyList) {
        int refundTicket = raffle.getTicketNum();

        List<Long> userIds = applyList.stream()
                .map(apply -> apply.getUser().getId())
                .collect(Collectors.toList());

        if (!userIds.isEmpty()) {
            userRepository.batchUpdateTicketNum(refundTicket, userIds);
        }
    }

    private User getUser() {
        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        return userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

    }

    private Raffle getRaffle(Long raffleId) {
        return raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));
    }

    @Override
    public Map<String, Object> getDrawRaffle(Long raffleId) {

        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        if (raffleStatus == RaffleStatus.UNOPENED ||
                raffleStatus == RaffleStatus.ACTIVE) {
            throw new CustomException(ErrorStatus.DRAW_NOT_ENDED);
        }

        Map<String, Object> result = new HashMap<>();
        if (raffle.getUser().equals(user)) {
            Delivery delivery = raffle.getDelivery();

            result.put("drawDto", null);
            result.put("redirectUrl", delivery != null ?
                    String.format("/api/permit/delivery/%d/owner", delivery.getId()) :
                    String.format("/api/permit/raffles/%d/draw/owner/result", raffleId));

            return result;
        }

        List<Apply> applyList = applyRepository.findByRaffle(raffle);
        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        List<String> nicknameList = applyList.stream()
                .map(apply -> apply.getUser().getNickname())
                .collect(Collectors.toList());

        result.put("drawDto", toDrawDto(raffle, nicknameList));
        result.put("redirectUrl", null);
        return result;

    }

    @Override
    public DrawResponseDTO.WinnerDto getWinner(Long raffleId) {
        Raffle raffle = getRaffle(raffleId);

        return toWinnerDto(raffle);
    }

    @Override
    public DrawResponseDTO.RaffleResultDto getRaffleResult(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        if (!user.equals(raffle.getUser()))
            throw new CustomException(ErrorStatus.DRAW_NOT_OWNER);

        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        return toRaffleResultDto(raffle, applyNum * ticket);
    }

    @Override
    @Transactional
    public String selfDraw(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        if (!user.equals(raffle.getUser()))
            throw new CustomException(ErrorStatus.DRAW_NOT_OWNER);

        raffle.setRaffleStatus(RaffleStatus.ENDED);
        raffleRepository.save(raffle);

        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        Delivery delivery = draw(raffle, applyList);

        emailService.sendEmail(delivery);

        return String.format("/api/permit/delivery/%d/owner", delivery.getId());
    }

    @Override
    @Transactional
    public DrawResponseDTO.CancelDto forceCancel(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        if (!user.equals(raffle.getUser()))
            throw new CustomException(ErrorStatus.DRAW_NOT_OWNER);

        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        cancel(raffle, applyList);

        return DrawResponseDTO.CancelDto.builder()
                .raffleId(raffleId)
                .build();
    }
}
