package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.service.general.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/raffles/{raffleId}/apply")
public class ApplyController {

    private final ApplyService applyService;

    @GetMapping("")
    public ApiResponse<ApplyResponseDTO.EnterDto> enterRaffle(@PathVariable Long raffleId) {
        return applyService.getEnterRaffle(raffleId);
    }

    @PostMapping("/{userId}")
    public ApiResponse<ApplyResponseDTO.ApplyDto> applyRaffle(@PathVariable Long userId, @PathVariable Long raffleId) {
        return applyService.applyRaffle(userId, raffleId);
    }

    @GetMapping("/{userId}/success")
    public ApiResponse<ApplyResponseDTO.SuccessDto> successApply(@PathVariable Long userId, @PathVariable Long raffleId) {
        return applyService.successApply(userId, raffleId);
    }

    @GetMapping("/{userId}/fail")
    public ApiResponse<ApplyResponseDTO.FailDto> failApply(@PathVariable Long userId, @PathVariable Long raffleId) {
        return applyService.failApply(userId, raffleId);
    }
}
