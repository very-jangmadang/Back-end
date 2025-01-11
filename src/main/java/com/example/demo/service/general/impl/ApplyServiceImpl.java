package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
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

@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;

    @Transactional(readOnly = true)
    public ApiResponse<ApplyResponseDTO.EnterDto> getEnterRaffle(Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new RuntimeException("Raffle not found with id: " + raffleId));

        ApplyResponseDTO.EnterDto enterDto = new ApplyResponseDTO.EnterDto(
                "image url",
                raffle.getName(),
                raffle.getTicketNum()
        );

        return new ApiResponse<>(
                true,
                "COMMON200",
                "성공입니다",
                enterDto
        );
    }

    @Transactional
    public ApiResponse<ApplyResponseDTO.ApplyDto> applyRaffle(Long userId, Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new RuntimeException("Raffle not found with id: " + raffleId));
        int raffleTicket = raffle.getTicketNum();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        int userTicket = user.getTicket_num();

        if (raffleTicket <= userTicket) {
            Apply apply = Apply.builder()
                    .raffle(raffle)
                    .user(user)
                    .build();
            applyRepository.save(apply);

            ApplyResponseDTO.ApplyDto applyDto = new ApplyResponseDTO.ApplyDto("/raffles/{raffle-id}/apply/{user-id}/success");

            return new ApiResponse<>(
                    true,
                    "COMMON200",
                    "성공입니다",
                    applyDto
            );
        } else {
            ApplyResponseDTO.ApplyDto applyDto = new ApplyResponseDTO.ApplyDto("/raffles/{raffle-id}/apply/{user-id}/fail");

            return new ApiResponse<>(
                    false,
                    "APPLY4001",
                    "보유 티켓 수가 부족합니다.",
                    applyDto
            );
        }
    }

    public ApiResponse<ApplyResponseDTO.SuccessDto> successApply(Long userId, Long raffleId) {
        ApplyResponseDTO.SuccessDto successDto = new ApplyResponseDTO.SuccessDto("image url");

        return new ApiResponse<>(
                true,
                "COMMON200",
                "성공입니다",
                successDto
        );
    }

    public ApiResponse<ApplyResponseDTO.FailDto> failApply(Long userId, Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new RuntimeException("Raffle not found with id: " + raffleId));
        int raffleTicket = raffle.getTicketNum();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        int userTicket = user.getTicket_num();

        int missingTicket = raffleTicket - userTicket;
        ApplyResponseDTO.FailDto failDto = new ApplyResponseDTO.FailDto(
                raffle.getName(),
                missingTicket
        );

        return new ApiResponse<>(
                false,
                "APPLY4001",
                "보유 티켓 수가 부족합니다",
                failDto
        );
    }
}