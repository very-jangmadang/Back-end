package com.example.demo.domain.dto.Delivery;

import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.base.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DeliveryResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryDto {
        private Long raffleId;
        private Long winnerId;
        private DeliveryStatus deliveryStatus;
        private LocalDateTime addressDeadline;
        private LocalDateTime shippingDeadline;
        private BigDecimal shippingFee;
        private boolean isShippingExtended;
        private String invoiceNumber;
        private MypageResponseDTO.AddressDto address;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDto {
        private Long deliveryId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitDto {
        private Long deliveryId;
        private LocalDateTime addressDeadline;
        private LocalDateTime shippingDeadline;
        private DeliveryStatus deliveryStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDto {
        private Long raffleId;
        private Long winnerId;
        private Long deliveryId;
        private int minTicket;
        private int applyTicket;
        private BigDecimal finalAmount;
        private DeliveryStatus deliveryStatus;
        private LocalDateTime shippingDeadline;
        private boolean isAddressExtended;
        private MypageResponseDTO.AddressDto address;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingDto {
        private Long deliveryId;
        private Long raffleId;
        private Long winnerId;
        private Long addressId;
    }

}
