package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.service.general.DrawService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/raffles")
public class DrawController {

    private final DrawService drawService;

    @GetMapping("/{raffleId}/draw")
    public ApiResponse<?> drawRaffle(@PathVariable Long raffleId, HttpServletResponse response) throws IOException {

        DrawResponseDTO.RaffleResult result = drawService.getDrawRaffle(raffleId);
        DrawResponseDTO.DrawDto drawDto = result.getDrawDto();
        String redirectUrl = result.getRedirectUrl();

        if (drawDto == null){
            response.sendRedirect(redirectUrl);
            return null;
        }

        return ApiResponse.of(_OK, drawDto);
    }

    @GetMapping("/{raffleId}/result")
    public ApiResponse<DrawResponseDTO.ResultDto> getResult(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getResult(raffleId));
    }

    @PostMapping("/{raffleId}/draw")
    public void selfDraw(@PathVariable Long raffleId, HttpServletResponse response) throws IOException {

        String redirectUrl = drawService.selfDraw(raffleId);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/{raffleId}/cancel")
    public ApiResponse<DrawResponseDTO.CancelDto> cancelDraw(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.forceCancel(raffleId));
    }

}
