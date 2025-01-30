package com.example.demo.service.general;

import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import org.springframework.security.core.Authentication;

public interface DeliveryService {
    DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId, Authentication authentication);

    DeliveryResponseDTO.ResponseDto setAddress(Long deliveryId, Authentication authentication);

    DeliveryResponseDTO.ResponseDto complete(Long deliveryId, Authentication authentication);

    DeliveryResponseDTO.WaitDto waitShipping(Long deliveryId, Authentication authentication);

    String cancel(Long deliveryId, Authentication authentication);

    DeliveryResponseDTO.ResultDto getResult(Long deliveryId, Authentication authentication);

    DeliveryResponseDTO.ResponseDto addInvoice(Long deliveryId, Authentication authentication, DeliveryRequestDTO deliveryRequestDTO);

    DeliveryResponseDTO.WaitDto waitAddress(Long deliveryId, Authentication authentication);

}
