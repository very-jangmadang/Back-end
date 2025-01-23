package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
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
    private final HttpServletResponse response;

    @GetMapping("/{raffleId}/draw")
    public ApiResponse<?> drawRaffle(@PathVariable Long raffleId) {

        Map<String, Object> result = drawService.getDrawRaffle(raffleId);
        DrawResponseDTO.DrawDto drawDto = (DrawResponseDTO.DrawDto) result.get("drawDto");
        Long deliveryId = (Long) result.get("deliveryId");

        if (deliveryId != null) {
            try {
                String redirectUrl = String.format("/api/permit/delivery/%d/owner", deliveryId);
                response.sendRedirect(redirectUrl);

                return null;
            } catch (IOException e) {
                throw new CustomException(ErrorStatus.DRAW_OWNER_REDIRECT_FAILED);
            }
        } else if (drawDto == null) {
            try {
                String redirectUrl = String.format("/api/permit/raffles/%d/draw/owner/result", raffleId);
                response.sendRedirect(redirectUrl);

                return null;
            } catch (IOException e) {
                throw new CustomException(ErrorStatus.DRAW_OWNER_REDIRECT_FAILED);
            }

        }

        return ApiResponse.of(_OK, drawDto);

    }

    @GetMapping("/{raffleId}/draw/result")
    public ApiResponse<DrawResponseDTO.WinnerDto> getWinner(
            @PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getWinner(raffleId));
    }

    @GetMapping("/{raffleId}/draw/owner/result")
    public ApiResponse<DrawResponseDTO.RaffleResultDto> getResult(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getResult(raffleId));
    }

    @GetMapping("/{raffleId}/draw/owner/selfdraw")
    public ApiResponse<?> selfDraw(@PathVariable Long raffleId) {

        Long deliveryId = drawService.selfDraw(raffleId);

        try {
            String redirectUrl = String.format("/api/permit/delivery/%d/owner", deliveryId);
            response.sendRedirect(redirectUrl);

            return null;
        } catch (IOException e) {
            throw new CustomException(ErrorStatus.DRAW_OWNER_REDIRECT_FAILED);
        }
    }

}
