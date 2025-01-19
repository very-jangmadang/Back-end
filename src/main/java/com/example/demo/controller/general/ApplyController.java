package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.ApplyResponseDTO;
import com.example.demo.service.general.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/raffles")
public class ApplyController {

    private final ApplyService applyService;

    @GetMapping("/{raffleId}/apply")
    public ApiResponse<ApplyResponseDTO.EnterDto> enterRaffle(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, applyService.getEnterRaffle(raffleId));

    }

    @PostMapping("/{raffleId}/apply/{userId}")
    public ApiResponse<ApplyResponseDTO.ApplyDto> applyRaffle(
            @PathVariable Long raffleId, @PathVariable Long userId) {

        return ApiResponse.of(_OK, applyService.applyRaffle(raffleId, userId));

    }

}
