package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Payment.CancelResponse;
import com.example.demo.service.general.DeliveryService;
import com.example.demo.service.general.KakaoPayService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    // Yoon - 시작


    private final KakaoPayService kakaoPayService;
    private final BaseController baseController;
    // 결제 취소 API
    @PostMapping("/cancel")
    public ApiResponse<CancelResponse> cancelPayment() {
        String userId = baseController.getCurrentUserEmail();
        return kakaoPayService.cancelPayment(userId);
    }

    
    // Yoon - 끝

    @Operation(summary = "당첨자 - 배송 정보 확인하기")
    @GetMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.DeliveryDto> getDelivery(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.getDelivery(deliveryId));
    }

    @Operation(summary = "당첨자 - 배송지 등록하기")
    @PostMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> setAddress(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.setAddress(deliveryId));
    }

    @Operation(summary = "당첨자 - 배송비 결제 완료하기")
    @PostMapping("/{deliveryId}/winner/complete")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> complete(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.complete(deliveryId));
    }

    @Operation(summary = "당첨자 - 운송장 입력 기한 연장하기")
    @PostMapping("{deliveryId}/winner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitShipping(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.waitShipping(deliveryId));
    }

    @Operation(summary = "당첨자 - 당첨 취소하기")
    @PostMapping("{deliveryId}/winner/cancel")
    public void cancel(
            @PathVariable Long deliveryId, HttpServletResponse response) throws IOException {

        String redirectUrl = deliveryService.cancel(deliveryId);
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "당첨자 - 배송 완료 처리하기")
    @PostMapping("{deliveryId}/winner/success")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> success(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.success(deliveryId));
    }

    @Operation(summary = "개최자 - 배송 정보 확인하기")
    @GetMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResultDto> getResult(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.getResult(deliveryId));
    }

    @Operation(summary = "개최자 - 운송장 입력하기")
    @PostMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> addInvoice(
            @PathVariable Long deliveryId, @RequestBody DeliveryRequestDTO deliveryRequestDTO) {

        return ApiResponse.of(_OK, deliveryService.addInvoice(deliveryId, deliveryRequestDTO));
    }

    @Operation(summary = "개최자 - 배송지 입력 기한 연장하기")
    @PostMapping("{deliveryId}/owner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitAddress(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.waitAddress(deliveryId));
    }

}
