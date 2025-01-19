package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
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
@Transactional(readOnly = true)
public class ApplyServiceImpl implements ApplyService {

    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;


    public ApplyResponseDTO.EnterDto getEnterRaffle(Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        return toEnterDto(raffle);
    }

    @Transactional
    public ApplyResponseDTO.ApplyDto applyRaffle(Long raffleId, Long userId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));
        int raffleTicket = raffle.getTicketNum();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        int userTicket = user.getTicket_num();

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(raffle.getEndAt()) || now.isEqual(raffle.getEndAt()))
            throw new CustomException(ErrorStatus.APPLY_FINISHED_RAFFLE);

        if (raffleTicket > userTicket)
            throw new CustomException(ErrorStatus.APPLY_INSUFFICIENT_TICKET);

        Apply apply = Apply.builder()
                .raffle(raffle)
                .user(user)
                .build();
        applyRepository.save(apply);

        return toApplyDto(apply);
    }

}