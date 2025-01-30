package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.service.general.DeliveryService;
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

    @GetMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.DeliveryDto> getDelivery(
            @PathVariable Long deliveryId, Authentication authentication) {

        return ApiResponse.of(_OK, deliveryService.getDelivery(deliveryId, authentication));
    }

    @PostMapping("/{deliveryId}/winner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> setAddress(
            @PathVariable Long deliveryId, Authentication authentication) {

        return ApiResponse.of(_OK, deliveryService.setAddress(deliveryId, authentication));
    }

    @PostMapping("/{deliveryId}/winner/complete")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> complete(
            @PathVariable Long deliveryId, Authentication authentication) {

        return ApiResponse.of(_OK, deliveryService.complete(deliveryId, authentication));
    }

    @PostMapping("{deliveryId}/winner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitShipping(
            @PathVariable Long deliveryId, Authentication authentication) {

        return ApiResponse.of(_OK, deliveryService.waitShipping(deliveryId, authentication));
    }

    @PostMapping("{deliveryId}/winner/cancel")
    public void cancel(
            @PathVariable Long deliveryId, Authentication authentication, HttpServletResponse response) throws IOException {

        String redirectUrl = deliveryService.cancel(deliveryId, authentication);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResultDto> getResult(
            @PathVariable Long deliveryId, Authentication authentication) {

        return ApiResponse.of(_OK, deliveryService.getResult(deliveryId, authentication));
    }

    @PostMapping("{deliveryId}/owner")
    public ApiResponse<DeliveryResponseDTO.ResponseDto> addInvoice(
            @PathVariable Long deliveryId, Authentication authentication, @RequestBody DeliveryRequestDTO deliveryRequestDTO) {

        return ApiResponse.of(_OK, deliveryService.addInvoice(deliveryId, authentication, deliveryRequestDTO));
    }

    @PostMapping("{deliveryId}/owner/wait")
    public ApiResponse<DeliveryResponseDTO.WaitDto> WaitAddress(
            @PathVariable Long deliveryId, Authentication authentication) {

        return ApiResponse.of(_OK, deliveryService.waitAddress(deliveryId, authentication));
    }

}
