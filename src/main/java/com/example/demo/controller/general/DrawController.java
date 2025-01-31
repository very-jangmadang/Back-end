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
            @PathVariable Long raffleId, Authentication authentication, HttpServletResponse response) throws IOException {

        if(authentication == null || !authentication.isAuthenticated())
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);

        DrawResponseDTO.RaffleResult result = drawService.getDrawRaffle(raffleId, Long.parseLong(authentication.getName()));
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
    public ApiResponse<DrawResponseDTO.ResultDto> getResult(
            @PathVariable Long raffleId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, drawService.getResult(raffleId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "개최자 - 미추첨 래플 수동 추첨하기")
    @PostMapping("/{raffleId}/draw")
    public ApiResponse<?> selfDraw(
            @PathVariable Long raffleId, Authentication authentication, HttpServletResponse response) throws IOException {

        if(authentication == null || !authentication.isAuthenticated())
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);

        String redirectUrl = drawService.selfDraw(raffleId, Long.parseLong(authentication.getName()));
        response.sendRedirect(redirectUrl);
        return null;
    }

    @Operation(summary = "개최자 - 래플 종료하기")
    @GetMapping("/{raffleId}/cancel")
    public ApiResponse<DrawResponseDTO.CancelDto> cancelDraw(
            @PathVariable Long raffleId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, drawService.forceCancel(raffleId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "개최자 - 래플 재추첨하기")
    @PostMapping("{raffleId}/redraw")
    public ApiResponse<?> redraw(
            @PathVariable Long raffleId, Authentication authentication, HttpServletResponse response) throws IOException {

        if(authentication == null || !authentication.isAuthenticated())
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);

        String redirectUrl = drawService.redraw(raffleId, Long.parseLong(authentication.getName()));
        response.sendRedirect(redirectUrl);
        return null;
    }

}
