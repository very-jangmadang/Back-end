package com.example.demo.service.general;

import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;

public interface DeliveryService {
    DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId, Long userId);

    DeliveryResponseDTO.ResponseDto setAddress(Long deliveryId, Long userId);

    DeliveryResponseDTO.ResponseDto complete(Long deliveryId, Long userId);

    DeliveryResponseDTO.WaitDto waitShipping(Long deliveryId, Long userId);

    String cancel(Long deliveryId, Long userId);

    DeliveryResponseDTO.ResultDto getResult(Long deliveryId, Long userId);

    DeliveryResponseDTO.ResponseDto addInvoice(Long deliveryId, Long userId, DeliveryRequestDTO deliveryRequestDTO);

    DeliveryResponseDTO.WaitDto waitAddress(Long deliveryId, Long userId);

}
