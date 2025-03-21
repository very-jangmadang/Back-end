package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Draw.DrawResponseDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.service.general.DrawService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/raffles")
public class DrawController {

    private final DrawService drawService;

    @Operation(summary = "응모자 - 래플 결과 확인하기")
    @GetMapping("/{raffleId}/draw")
    public ApiResponse<DrawResponseDTO.DrawDto> drawRaffle(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getDrawRaffle(raffleId));
    }

    @Operation(summary = "응모자 - 래플 결과 조회 완료하기")
    @PostMapping("/{raffleId}/check")
    public ApiResponse<?> checkRaffle(@PathVariable Long raffleId) {

        drawService.checkRaffle(raffleId);

        return ApiResponse.of(_OK, null);
    }

    @Operation(summary = "개최자 - 래플 결과 확인하기")
    @GetMapping("/{raffleId}/result")
    public ApiResponse<DrawResponseDTO.ResultDto> getResult(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getResult(raffleId));
    }

    @Operation(summary = "개최자 - 미추첨 래플 수동 추첨하기")
    @PostMapping("/{raffleId}/draw")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> selfDraw(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.selfDraw(raffleId));
    }

    @Operation(summary = "개최자 - 래플 종료하기")
    @PostMapping("/{raffleId}/cancel")
    public ApiResponse<RaffleResponseDTO.ResponseDTO> cancelDraw(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.forceCancel(raffleId));
    }

    @Operation(summary = "개최자 - 래플 재추첨하기")
    @PostMapping("{raffleId}/redraw")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> redraw(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.redraw(raffleId));
    }

}
