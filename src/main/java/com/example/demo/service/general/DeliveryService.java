package com.example.demo.service.general;

import com.example.demo.domain.dto.Delivery.DeliveryRequestDTO;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;

public interface DeliveryService {
    DeliveryResponseDTO.DeliveryDto getDelivery(Long deliveryId);

    DeliveryResponseDTO.AddressChoiceDto chooseAddress(Long deliveryId, DeliveryRequestDTO.WinnerDTO winnerDTO);

    void waitShipping(Long deliveryId);

    DeliveryResponseDTO.ResultDto getResult(Long deliveryId);

    DeliveryResponseDTO.ShippingDto addInvoice(Long deliveryId, DeliveryRequestDTO.OwnerDTO ownerDTO);

    void waitAddress(Long deliveryId);
}
