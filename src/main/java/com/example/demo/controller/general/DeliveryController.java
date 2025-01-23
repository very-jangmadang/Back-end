package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.service.general.DeliveryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permit/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.DeliveryDto> getDelivery(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.getDelivery(deliveryId));
    }

    @PostMapping("{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.AddressChoiceDto> chooseAddress(
            @PathVariable Long deliveryId, @RequestBody DeliveryRequestDTO.WinnerDTO winnerDTO) {

        return ApiResponse.of(_OK, deliveryService.chooseAddress(deliveryId, winnerDTO));
    }

    @GetMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResultDto> getResult(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.getResult(deliveryId));
    }

    @PostMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ShippingDto> addInvoice(
            @PathVariable Long deliveryId, @RequestBody DeliveryRequestDTO.OwnerDTO ownerDTO) {

        return ApiResponse.of(_OK, deliveryService.addInvoice(deliveryId, ownerDTO));
    }

    @GetMapping("{deliveryId}/owner/wait")
    public ApiResponse<DeliveryResponseDTO.ResultDto> WaitAddress(@PathVariable Long deliveryId) {

        return ApiResponse.of(_OK, deliveryService.waitAddress(deliveryId));
    }

}
