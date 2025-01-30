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
        private LocalDateTime deadline;
        private BigDecimal shippingFee;
        private List<MypageResponseDTO.AddressDto> addressList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressChoiceDto {
        private Long deliveryId;
        private Long raffleId;
        private Long winnerId;
        private Long addressId;
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
        private DeliveryStatus status;
        private String recipientName;
        private String addressDetail;
        private String phoneNumber;
        private LocalDateTime deadline;
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
