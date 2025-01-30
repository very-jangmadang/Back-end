package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.demo.domain.converter.ApplyConverter.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyServiceImpl implements ApplyService {

    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;

    public ApplyResponseDTO applyRaffle(Long raffleId) {

        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        int userTicket = user.getTicket_num();

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));
        int raffleTicket = raffle.getTicketNum();

        if (raffle.getRaffleStatus() == RaffleStatus.UNOPENED)
            throw new CustomException(ErrorStatus.APPLY_UNOPENED_RAFFLE);
        if (raffle.getRaffleStatus() != RaffleStatus.ACTIVE)
            throw new CustomException(ErrorStatus.APPLY_FINISHED_RAFFLE);

        if (raffle.getUser().getId().equals(user.getId()))
            throw new CustomException(ErrorStatus.APPLY_SELF_RAFFLE);

        if (applyRepository.existsByRaffleAndUser(raffle, user))
            throw new CustomException(ErrorStatus.APPLY_ALREADY_APPLIED);

        if (raffleTicket > userTicket)
            throw new CustomException(ErrorStatus.APPLY_INSUFFICIENT_TICKET);

        user.setTicket_num(userTicket - raffleTicket);

        Apply apply = Apply.builder()
                .raffle(raffle)
                .user(user)
                .build();
        applyRepository.save(apply);

        return toApplyDto(apply);
    }

}