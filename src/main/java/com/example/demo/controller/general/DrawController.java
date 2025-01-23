package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Draw.DrawRequestDTO;
import com.example.demo.domain.dto.Draw.DrawResponseDTO;
import com.example.demo.domain.dto.Like.LikeRequestDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.service.general.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/raffles")
public class DrawController {

    private final DrawService drawService;

    @GetMapping("/{raffleId}/draw")
    public ApiResponse<DrawResponseDTO.DrawDto> drawRaffle(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getDrawRaffle(raffleId));

    }

    @GetMapping("/{raffleId}/draw/result")
    public ApiResponse<DrawResponseDTO.WinnerDto> getWinner(
            @PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getWinner(raffleId));
    }

    @GetMapping("/{raffleId}/draw/delivery")
    public ApiResponse<DrawResponseDTO.DeliveryDto> drawDelivery(@PathVariable Long raffleId) {

        return ApiResponse.of(_OK, drawService.getDelivery(raffleId));
    }

    @PostMapping("{raffleId}/draw/delivery")
    public ApiResponse<DrawResponseDTO.AddressChoiceDto> chooseAddress(
            @PathVariable Long raffleId, @RequestBody DrawRequestDTO drawRequestDTO) {

        return ApiResponse.of(_OK, drawService.chooseAddress(raffleId, drawRequestDTO));
    }

}
