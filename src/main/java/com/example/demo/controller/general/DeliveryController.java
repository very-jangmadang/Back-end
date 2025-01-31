package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.service.general.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "당첨자 - 배송 정보 확인하기")
    @GetMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.DeliveryDto> getDelivery(
            @PathVariable Long deliveryId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.getDelivery(deliveryId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "당첨자 - 배송지 등록하기")
    @PostMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> setAddress(
            @PathVariable Long deliveryId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.setAddress(deliveryId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "당첨자 - 배송비 결제 완료하기")
    @PostMapping("/{deliveryId}/winner/complete")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> complete(
            @PathVariable Long deliveryId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.complete(deliveryId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "당첨자 - 운송장 입력 기한 연장하기")
    @PostMapping("{deliveryId}/winner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitShipping(
            @PathVariable Long deliveryId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.waitShipping(deliveryId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "당첨자 - 당첨 취소하기")
    @PostMapping("{deliveryId}/winner/cancel")
    public ApiResponse<?> cancel(
            @PathVariable Long deliveryId, Authentication authentication, HttpServletResponse response) throws IOException {

        if(authentication == null || !authentication.isAuthenticated())
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);

        String redirectUrl = deliveryService.cancel(deliveryId, Long.parseLong(authentication.getName()));
        response.sendRedirect(redirectUrl);
        return null;
    }

    @Operation(summary = "개최자 - 배송 정보 확인하기")
    @GetMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResultDto> getResult(
            @PathVariable Long deliveryId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.getResult(deliveryId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "개최자 - 운송장 입력하기")
    @PostMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> addInvoice(
            @PathVariable Long deliveryId, Authentication authentication, @RequestBody DeliveryRequestDTO deliveryRequestDTO) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.addInvoice(
                    deliveryId, Long.parseLong(authentication.getName()), deliveryRequestDTO));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

    @Operation(summary = "개최자 - 배송지 입력 기한 연장하기")
    @PostMapping("{deliveryId}/owner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitAddress(
            @PathVariable Long deliveryId, Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated())
            return ApiResponse.of(_OK, deliveryService.waitAddress(deliveryId, Long.parseLong(authentication.getName())));
        else
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
    }

}
