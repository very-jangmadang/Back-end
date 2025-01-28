package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.service.general.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.demo.base.status.SuccessStatus.REDIRECT_SUCCESS;
import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/raffles")
public class DrawController {

    private final DrawService drawService;

    @GetMapping("/{raffleId}/draw")
    public ApiResponse<?> drawRaffle(@PathVariable Long raffleId) {

        Map<String, Object> result = drawService.getDrawRaffle(raffleId);
        DrawResponseDTO.DrawDto drawDto = (DrawResponseDTO.DrawDto) result.get("drawDto");
        String redirectUrl = (String) result.get("redirectUrl");

        if (drawDto == null)
            return ApiResponse.of(REDIRECT_SUCCESS, Map.of("redirectUrl", redirectUrl));

        return ApiResponse.of(_OK, drawDto);

    }

    @GetMapping("/{raffleId}/draw/result")
    public ApiResponse<DrawResponseDTO.WinnerDto> getWinner(
            @PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getWinner(raffleId));
    }

    @GetMapping("/{raffleId}/draw/owner/result")
    public ApiResponse<DrawResponseDTO.RaffleResultDto> getRaffleResult(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getRaffleResult(raffleId));
    }

    @GetMapping("/{raffleId}/draw/owner/draw")
    public ApiResponse<Map<String, String>> selfDraw(@PathVariable Long raffleId) {
        String redirectUrl = drawService.selfDraw(raffleId);

        return ApiResponse.of(REDIRECT_SUCCESS, Map.of("redirectUrl", redirectUrl));
    }

    @GetMapping("/{raffleId}/draw/owner/cancel")
    public ApiResponse<DrawResponseDTO.CancelDto> cancelDraw(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.forceCancel(raffleId));
    }

}
