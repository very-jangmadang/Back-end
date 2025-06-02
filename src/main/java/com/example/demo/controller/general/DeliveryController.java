package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.service.general.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "당첨자 - 배송 정보 확인하기")
    @GetMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.WinnerResultDto> getDelivery(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.getDelivery(deliveryId));
    }

    @Operation(summary = "당첨자 - 배송지 등록하기")
    @PostMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> setAddress(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.setAddress(deliveryId));
    }

    @Operation(summary = "당첨자 - 운송장 입력 기한 연장하기")
    @PostMapping("{deliveryId}/winner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitShipping(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.waitShipping(deliveryId));
    }

    @Operation(summary = "당첨자 - 당첨 취소하기")
    @PostMapping("{deliveryId}/winner/cancel")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> cancel(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.cancel(deliveryId));
    }

    @Operation(summary = "당첨자 - 배송 완료 처리하기")
    @PostMapping("{deliveryId}/winner/success")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> success(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.success(deliveryId));
    }

    @Operation(summary = "개최자 - 배송 정보 확인하기")
    @GetMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.OwnerResultDto> getResult(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.getResult(deliveryId));
    }

    @Operation(summary = "개최자 - 운송장 입력하기")
    @PostMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> addInvoice(
            @PathVariable Long deliveryId, @RequestBody DeliveryRequestDTO deliveryRequestDTO) {

        return ApiResponse.of(_OK, deliveryService.addInvoice(deliveryId, deliveryRequestDTO));
    }

    @Operation(summary = "개최자 - 운송장 수정하기")
    @PatchMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> editInvoice(
            @PathVariable Long deliveryId, @RequestBody DeliveryRequestDTO deliveryRequestDTO) {

        return ApiResponse.of(_OK, deliveryService.editInvoice(deliveryId, deliveryRequestDTO));
    }

    @Operation(summary = "개최자 - 배송지 입력 기한 연장하기")
    @PostMapping("{deliveryId}/owner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitAddress(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.waitAddress(deliveryId));
    }

}
