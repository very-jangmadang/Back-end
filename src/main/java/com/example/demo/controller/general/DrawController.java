package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.service.general.DrawService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/raffles")
public class DrawController {

    private final DrawService drawService;

    @Operation(summary = "래플 결과 확인하기")
    @GetMapping("/{raffleId}/draw")
    public ApiResponse<?> drawRaffle(
            @PathVariable Long raffleId, HttpServletResponse response) throws IOException {

        DrawResponseDTO.RaffleResult result = drawService.getDrawRaffle(raffleId);
        DrawResponseDTO.DrawDto drawDto = result.getDrawDto();
        String redirectUrl = result.getRedirectUrl();

        if (drawDto == null){
            response.sendRedirect(redirectUrl);
            return null;
        }

        return ApiResponse.of(_OK, drawDto);
    }

    @Operation(summary = "개최자 - 래플 결과 확인하기")
    @GetMapping("/{raffleId}/result")
    public ApiResponse<DrawResponseDTO.ResultDto> getResult(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getResult(raffleId));
    }

    @Operation(summary = "개최자 - 미추첨 래플 수동 추첨하기")
    @PostMapping("/{raffleId}/draw")
    public void selfDraw(
            @PathVariable Long raffleId, HttpServletResponse response) throws IOException {

        String redirectUrl = drawService.selfDraw(raffleId);
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "개최자 - 래플 종료하기")
    @GetMapping("/{raffleId}/cancel")
    public ApiResponse<DrawResponseDTO.CancelDto> cancelDraw(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.forceCancel(raffleId));
    }

    @Operation(summary = "개최자 - 래플 재추첨하기")
    @PostMapping("{raffleId}/redraw")
    public void redraw(
            @PathVariable Long raffleId, HttpServletResponse response) throws IOException {

        String redirectUrl = drawService.redraw(raffleId);
        response.sendRedirect(redirectUrl);
    }

}
