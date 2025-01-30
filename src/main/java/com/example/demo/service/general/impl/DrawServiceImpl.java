package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.DrawService;
import com.example.demo.service.general.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

        Random random = new Random(System.nanoTime());
        int randomIndex = random.nextInt(applyList.size());

        User winner = applyList.get(randomIndex).getUser();
        raffle.setWinner(winner);
        raffle.setRaffleStatus(RaffleStatus.ENDED);

        Delivery delivery = toDelivery(raffle);
        delivery.setAddressDeadline();
        deliveryRepository.save(delivery);

        raffle.addDelivery(delivery);
        raffleRepository.save(raffle);
      
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

        raffle.setRaffleStatus(RaffleStatus.FINISHED);
        raffleRepository.save(raffle);
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
    public DrawResponseDTO.RaffleResult getDrawRaffle(Long raffleId) {

        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleStatus(raffleStatus);

        Delivery delivery = deliveryRepository.findByRaffleAndWinner(raffle, raffle.getWinner());

        // 개최자인 경우
        if (raffle.getUser().equals(user)) {
            String redirectUrl = delivery != null ?
                    String.format(Constants.DELIVERY_OWNER_URL, delivery.getId()) :
                    String.format(Constants.RAFFLE_OWNER_URL, raffleId);

            return DrawResponseDTO.RaffleResult.builder()
                    .drawDto(null)
                    .redirectUrl(redirectUrl)
                    .build();
        }

        if (!applyRepository.existsByRaffleAndUser(raffle, user))
            throw new CustomException(ErrorStatus.DRAW_NOT_IN);

        if (raffleStatus == RaffleStatus.UNFULFILLED)
            throw new CustomException(ErrorStatus.DRAW_PENDING);

        List<Apply> applyList = applyRepository.findByRaffle(raffle);
        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        Set<String> nicknameSet = applyList.stream()
                .map(apply -> apply.getUser().getNickname())
                .filter(nickname -> !nickname.equals(user.getNickname()) &&
                        !nickname.equals(raffle.getWinner().getNickname()))
                .limit(Constants.MAX_NICKNAMES - 2)
                .collect(Collectors.toSet());

        nicknameSet.add(user.getNickname());
        nicknameSet.add(raffle.getWinner().getNickname());

        boolean isWin = raffle.getWinner().equals(user);

        return DrawResponseDTO.RaffleResult.builder()
                .drawDto(toDrawDto(delivery, nicknameSet, isWin))
                .redirectUrl(null)
                .build();
    }

    @Override
    public DrawResponseDTO.ResultDto getResult(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleStatus(raffleStatus);
        if (raffleStatus == RaffleStatus.ENDED)
            throw new CustomException(ErrorStatus.DRAW_COMPLETED);

        int applyNum = applyRepository.countByRaffle(raffle);
        int ticket = raffle.getTicketNum();

        return toResultDto(raffle, applyNum * ticket);
    }

    @Override
    @Transactional
    public String selfDraw(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleStatus(raffleStatus);
        switch (raffleStatus) {
            case ENDED:
                throw new CustomException(ErrorStatus.DRAW_COMPLETED);
            case FINISHED:
                throw new CustomException(ErrorStatus.DRAW_FINISHED);
        }

        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        raffle.setRaffleStatus(RaffleStatus.ENDED);
        raffleRepository.save(raffle);

        Delivery delivery = draw(raffle, applyList);

        emailService.sendEmail(delivery);

        return String.format("/api/permit/delivery/%d/owner", delivery.getId());
    }

    @Override
    @Transactional
    public DrawResponseDTO.CancelDto forceCancel(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleStatus(raffleStatus);
        if (raffleStatus == RaffleStatus.FINISHED)
            throw new CustomException(ErrorStatus.DRAW_FINISHED);

        if (raffleStatus == RaffleStatus.ENDED) {
            Delivery delivery = deliveryRepository.findByRaffleAndWinner(raffle, raffle.getWinner());

            if (delivery != null) {
                DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

                if (deliveryStatus != DeliveryStatus.ADDRESS_EXPIRED &&
                        deliveryStatus != DeliveryStatus.CANCELLED)
                    throw new CustomException(ErrorStatus.CANCEL_FAIL);
            }
        }

        List<Apply> applyList = applyRepository.findByRaffle(raffle);
        cancel(raffle, applyList);

        return DrawResponseDTO.CancelDto.builder()
                .raffleId(raffleId)
                .build();
    }

    private void validateRaffleOwnership(User user, Raffle raffle) {
        if (!user.equals(raffle.getUser()))
            throw new CustomException(ErrorStatus.DRAW_NOT_OWNER);
    }

    private void validateRaffleStatus(RaffleStatus status) {
        if (status == RaffleStatus.UNOPENED || status == RaffleStatus.ACTIVE)
            throw new CustomException(ErrorStatus.DRAW_NOT_ENDED);
    }

}
