package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/raffles")
public class ApplyController {

    private final ApplyService applyService;
    private final RaffleRepository raffleRepository;

    @GetMapping("/{raffleId}/apply")
    public ApiResponse<ApplyResponseDTO.EnterDto> enterRaffle(@PathVariable Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new RuntimeException("Raffle not found with id: " + raffleId));

        ApplyResponseDTO.EnterDto enterDto = new ApplyResponseDTO.EnterDto(
                "image url",
                raffle.getName(),
                raffle.getTicketNum()
        );

        return ApiResponse.of(_OK, enterDto);
    }

    @PostMapping("/{raffleId}/apply/{userId}")
    public ApiResponse<ApplyResponseDTO.ApplyDto> applyRaffle(@PathVariable Long userId, @PathVariable Long raffleId) {
        return applyService.applyRaffle(userId, raffleId);
    }

    @GetMapping("/{raffleId}/apply/{userId}/success")
    public ApiResponse<ApplyResponseDTO.SuccessDto> successApply(@PathVariable Long userId, @PathVariable Long raffleId) {
        return applyService.successApply(userId, raffleId);
    }

    @GetMapping("/{raffleId}/apply/{userId}/fail")
    public ApiResponse<ApplyResponseDTO.FailDto> failApply(@PathVariable Long userId, @PathVariable Long raffleId) {
        return applyService.failApply(userId, raffleId);
    }
}
