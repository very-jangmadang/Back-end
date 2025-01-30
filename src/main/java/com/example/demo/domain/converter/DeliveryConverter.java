package com.example.demo.domain.converter;

import com.example.demo.base.Constants;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.DeliveryStatus;

import java.math.BigDecimal;

public class DeliveryConverter {

    public static Delivery toDelivery(Raffle raffle) {
        return Delivery.builder()
                .raffle(raffle)
                .user(raffle.getUser())
                .winner(raffle.getWinner())
                .deliveryStatus(DeliveryStatus.WAITING_ADDRESS)
                .isAddressExtended(false)
                .isShippingExtended(false)
                .build();
    }

    public static DeliveryResponseDTO.DeliveryDto toDeliveryDto(
            Delivery delivery, MypageResponseDTO.AddressDto addressDto) {

        return DeliveryResponseDTO.DeliveryDto.builder()
                .raffleId(delivery.getRaffle().getId())
                .winnerId(delivery.getWinner().getId())
                .deliveryStatus(delivery.getDeliveryStatus())
                .addressDeadline(delivery.getAddressDeadline())
                .shippingDeadline(delivery.getShippingDeadline())
                .shippingFee(delivery.getRaffle().getShippingFee())
                .isShippingExtended(delivery.isShippingExtended())
                .invoiceNumber(delivery.getInvoiceNumber())
                .address(addressDto)
                .build();
    }

    public static DeliveryResponseDTO.WaitDto toWaitDto(Delivery delivery) {
        return DeliveryResponseDTO.WaitDto.builder()
                .deliveryId(delivery.getId())
                .addressDeadline(delivery.getAddressDeadline())
                .shippingDeadline(delivery.getShippingDeadline())
                .deliveryStatus(delivery.getDeliveryStatus())
                .build();
    }

    public static DeliveryResponseDTO.ResultDto toResultDto(
            Delivery delivery, int applyTicket, MypageResponseDTO.AddressDto addressDto) {
        return DeliveryResponseDTO.ResultDto.builder()
                .raffleId(delivery.getRaffle().getId())
                .winnerId(delivery.getWinner().getId())
                .deliveryId(delivery.getId())
                .minTicket(delivery.getRaffle().getMinTicket())
                .applyTicket(applyTicket)
                .finalAmount(BigDecimal.valueOf(applyTicket).multiply(new BigDecimal("93")))
                .deliveryStatus(delivery.getDeliveryStatus())
                .shippingDeadline(delivery.getShippingDeadline())
                .isAddressExtended(delivery.isAddressExtended())
                .address(addressDto)
                .build();
    }

}
