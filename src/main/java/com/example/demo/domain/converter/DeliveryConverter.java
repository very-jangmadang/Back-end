package com.example.demo.domain.converter;

import com.example.demo.base.Constants;
import com.example.demo.domain.dto.Delivery.DeliveryResponseDTO;
import com.example.demo.domain.dto.MypageResponseDTO;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.util.List;

public class DeliveryConverter {

    public static Delivery toDelivery(Raffle raffle) {
        return Delivery.builder()
                .raffle(raffle)
                .user(raffle.getUser())
                .winner(raffle.getWinner())
                .deliveryStatus(DeliveryStatus.WAITING_ADDRESS)
                .addressDeadline(raffle.getEndAt().plusHours(Constants.ADDRESS_DEADLINE))
                .build();
    }

    public static DeliveryResponseDTO.DeliveryDto toDeliveryDto(
            Delivery delivery, List<MypageResponseDTO.AddressDto> addressList) {
        return DeliveryResponseDTO.DeliveryDto.builder()
                .raffleId(delivery.getRaffle().getId())
                .winnerId(delivery.getWinner().getId())
                .deadline(delivery.getAddressDeadline())
                .shippingFee(delivery.getRaffle().getShippingFee())
                .addressList(addressList)
                .build();
    }

    public static DeliveryResponseDTO.AddressChoiceDto toAddressChoiceDto(Delivery delivery) {
        return DeliveryResponseDTO.AddressChoiceDto.builder()
                .deliveryId(delivery.getId())
                .raffleId(delivery.getRaffle().getId())
                .winnerId(delivery.getWinner().getId())
                .addressId(delivery.getAddress().getId())
                .build();
    }

    public static DeliveryResponseDTO.ResultDto toResultDto(Delivery delivery, int applyTicket) {
        return DeliveryResponseDTO.ResultDto.builder()
                .raffleId(delivery.getRaffle().getId())
                .winnerId(delivery.getWinner().getId())
                .deliveryId(delivery.getId())
                .minTicket(delivery.getRaffle().getMinTicket())
                .applyTicket(applyTicket)
                .finalAmount(BigDecimal.valueOf(applyTicket).multiply(new BigDecimal("0.93")))
                .status(delivery.getDeliveryStatus())
                .recipientName(delivery.getAddress().getRecipientName())
                .addressDetail(delivery.getAddress().getAddressDetail())
                .phoneNumber((delivery.getAddress().getPhoneNumber()))
                .deadline(delivery.getShippingDeadline())
                .build();
    }

    public static DeliveryResponseDTO.ShippingDto toShippingDto(Delivery delivery) {
        return DeliveryResponseDTO.ShippingDto.builder()
                .deliveryId(delivery.getId())
                .raffleId(delivery.getRaffle().getId())
                .winnerId(delivery.getWinner().getId())
                .addressId(delivery.getAddress().getId())
                .build();
    }
}
