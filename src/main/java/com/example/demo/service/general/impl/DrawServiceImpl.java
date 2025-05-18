package com.example.demo.service.general.impl;

import com.example.demo.base.Constants;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Draw.DrawResponseDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.*;
import com.example.demo.service.general.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.domain.converter.DeliveryConverter.toDelivery;
import static com.example.demo.domain.converter.DeliveryConverter.toDeliveryResponseDto;
import static com.example.demo.domain.converter.DrawConverter.*;
import static com.example.demo.domain.converter.RaffleConverter.toRaffleResponseDTO;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplyRepository applyRepository;
    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SchedulerService schedulerService;
    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Delivery draw(Raffle raffle, List<Apply> applyList) {

        SecureRandom random = new SecureRandom();
        int randomIndex = random.nextInt(applyList.size());

        User winner = applyList.get(randomIndex).getUser();
        raffle.setWinner(winner);
        raffle.setRaffleStatus(RaffleStatus.ENDED);

        Delivery delivery = toDelivery(raffle);
        delivery.setAddressDeadline();

        raffle.addDelivery(delivery);
        deliveryRepository.save(delivery);

        emailService.sendWinnerPrizeEmail(delivery);

        schedulerService.scheduleDeliveryJob(delivery);
      
        return delivery;
    }

    @Override
    @Transactional
    public void cancel(Raffle raffle) {
        List<Apply> applyList = raffle.getApplyList();
        if (applyList == null || applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        int refundTicket = raffle.getTicketNum();

        List<Long> userIds = applyList.stream()
                .map(apply -> apply.getUser().getId())
                .collect(Collectors.toList());

        if (!userIds.isEmpty() && refundTicket > 0) {
            userRepository.batchUpdateTicketNum(refundTicket, userIds);
        }

        raffle.setRaffleStatus(RaffleStatus.CANCELLED);
    }

    @Override
    public DrawResponseDTO.DrawDto getDrawRaffle(Long raffleId) {

        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleEnd(raffleStatus);

        Delivery delivery = raffle.getDelivery().get(raffle.getDelivery().size() - 1);

        // 개최자인 경우
        if (raffle.getUser().equals(user))
            throw new CustomException(ErrorStatus.DRAW_OWNER);

        Apply userApply = applyRepository.findByRaffleAndUser(raffle, user);

        if (userApply == null)
            throw new CustomException(ErrorStatus.DRAW_NOT_IN);

        if (raffleStatus == RaffleStatus.UNFULFILLED)
            throw new CustomException(ErrorStatus.DRAW_PENDING);

        if (userApply.isChecked() && !user.equals(raffle.getWinner()))
            throw new CustomException(ErrorStatus.DRAW_ALREADY_CHECKED);

        List<Apply> applyList = raffle.getApplyList();
        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        Set<String> nicknameSet = new LinkedHashSet<>();

        if (raffle.getWinner().equals(user)) {
            nicknameSet.addAll(applyList.stream()
                    .map(apply -> apply.getUser().getNickname())
                    .filter(nickname -> !nickname.equals(user.getNickname()))
                    .limit(Constants.MAX_NICKNAMES - 1)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        } else {
            nicknameSet.add(user.getNickname());
            nicknameSet.addAll(applyList.stream()
                    .map(apply -> apply.getUser().getNickname())
                    .filter(nickname -> !nickname.equals(user.getNickname()) &&
                            !nickname.equals(raffle.getWinner().getNickname()))
                    .limit(Constants.MAX_NICKNAMES - 2)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        nicknameSet.add(raffle.getWinner().getNickname());

        boolean isWin = raffle.getWinner().equals(user);

        return toDrawDto(delivery, nicknameSet, isWin);
    }

    @Override
    @Transactional
    public void checkRaffle(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleEnd(raffleStatus);
        if (raffleStatus == RaffleStatus.UNFULFILLED)
            throw new CustomException(ErrorStatus.DRAW_PENDING);

        Apply apply = applyRepository.findByRaffleAndUser(raffle, user);

        if (apply == null)
            throw new CustomException(ErrorStatus.DRAW_NOT_IN);

        if (apply.isChecked())
            throw new CustomException(ErrorStatus.DRAW_ALREADY_CHECKED);

        apply.setChecked();
        if (user.equals(raffle.getWinner())){
            notificationService.sendWinnerForEndedRaffle(user,raffle);}
    }

    @Override
    public DrawResponseDTO.ResultDto getResult(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleEnd(raffleStatus);
        validateRaffleUnfulfilled(raffleStatus);

        int applyNum = raffle.getApplyList().size();
        int ticket = raffle.getTicketNum();

        return toResultDto(raffle, applyNum * ticket);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto selfDraw(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleEnd(raffleStatus);
        validateRaffleUnfulfilled(raffleStatus);

        schedulerService.cancelDrawJob(raffle);

        List<Apply> applyList = raffle.getApplyList();

        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        Delivery delivery = draw(raffle, applyList);

        return toDeliveryResponseDto(delivery.getId());
    }

    @Override
    @Transactional
    public RaffleResponseDTO.ResponseDTO forceCancel(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleEnd(raffleStatus);
        validateCancel(raffle, raffleStatus);

        schedulerService.cancelDrawJob(raffle);

        cancel(raffle);
        notificationService.sendWinnerForCancel(raffle);

        return toRaffleResponseDTO(raffle);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO.ResponseDto redraw(Long raffleId) {
        User user = getUser();
        Raffle raffle = getRaffle(raffleId);

        validateRaffleOwnership(user, raffle);

        RaffleStatus raffleStatus = raffle.getRaffleStatus();
        validateRaffleEnd(raffleStatus);
        if (raffleStatus == RaffleStatus.UNFULFILLED)
            throw new CustomException(ErrorStatus.DRAW_PENDING);
        validateCancel(raffle, raffleStatus);

        if (raffle.isRedrawn())
            throw new CustomException(ErrorStatus.DRAW_ALREADY_REDRAW);

        // 기존 당첨자에게 취소 알림 보내기
        User winner = raffle.getWinner();
        if (winner != null) {
            notificationService.sendWinnerForCancel(raffle);
        }


        List<Apply> applyList = raffle.getApplyList();

        applyList = applyList.stream()
                .filter(apply -> !apply.getUser().equals(winner))
                .collect(Collectors.toList());

        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        raffle.setIsRedrawn();

        Delivery delivery = draw(raffle, applyList);

        applyRepository.updateUncheckedByRaffle(raffle);

        return toDeliveryResponseDto(delivery.getId());
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
        return userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

    private Raffle getRaffle(Long raffleId) {
        return raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));
    }

    private void validateRaffleOwnership(User user, Raffle raffle) {
        if (!user.equals(raffle.getUser()))
            throw new CustomException(ErrorStatus.DRAW_NOT_OWNER);
    }

    private void validateRaffleEnd(RaffleStatus status) {
        if (status == RaffleStatus.UNOPENED || status == RaffleStatus.ACTIVE)
            throw new CustomException(ErrorStatus.DRAW_NOT_ENDED);
    }

    private void validateRaffleUnfulfilled(RaffleStatus status) {
        if (status != RaffleStatus.UNFULFILLED)
            throw new CustomException(ErrorStatus.DRAW_NOT_UNFULFILLED);
    }

    private void validateCancel(Raffle raffle, RaffleStatus raffleStatus) {
        if (raffleStatus == RaffleStatus.CANCELLED || raffleStatus == RaffleStatus.COMPLETED)
            throw new CustomException(ErrorStatus.DRAW_FINISHED);

        if (raffleStatus == RaffleStatus.ENDED) {
            List<Delivery> deliveryList = raffle.getDelivery();

            if (!deliveryList.isEmpty()) {
                Delivery delivery = deliveryList.get(deliveryList.size() - 1);
                DeliveryStatus deliveryStatus = delivery.getDeliveryStatus();

                if (deliveryStatus != DeliveryStatus.ADDRESS_EXPIRED)
                    throw new CustomException(ErrorStatus.RAFFLE_CANCEL_FAIL);

                schedulerService.cancelDeliveryJob(delivery, "Waiting");

                emailService.sendWinnerCancelEmail(delivery);

                delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);
            }
        }
    }
}
