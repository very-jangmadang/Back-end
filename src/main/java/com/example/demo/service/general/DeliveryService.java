package com.example.demo.service.general;

import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.entity.Delivery;

public interface DeliveryService {
    DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId);

    DeliveryResponseDTO.ResponseDto setAddress(Long deliveryId);

    DeliveryResponseDTO.WaitDto waitShipping(Long deliveryId);

    DeliveryResponseDTO.ResponseDto cancel(Long deliveryId);

    DeliveryResponseDTO.ResponseDto success(Long deliveryId);

    DeliveryResponseDTO.ResultDto getResult(Long deliveryId);

    DeliveryResponseDTO.ResponseDto addInvoice(Long deliveryId, DeliveryRequestDTO deliveryRequestDTO);

    DeliveryResponseDTO.WaitDto waitAddress(Long deliveryId);

    void finalize(Delivery delivery);

}
