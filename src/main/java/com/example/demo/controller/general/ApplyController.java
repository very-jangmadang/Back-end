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

    @PostMapping("/{raffleId}/apply")
    public ApiResponse<ApplyResponseDTO> applyRaffle(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, applyService.applyRaffle(raffleId));

    }

}
