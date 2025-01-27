package com.example.demo.domain.dto.Delivery;

import com.example.demo.entity.base.enums.CourierCompany;
import lombok.Getter;

public class DeliveryRequestDTO {

    @Getter
    public static class WinnerDTO {
        private Long addressId;
        private boolean agree;
    }

    @Getter
    public static class OwnerDTO {
//        private CourierCompany courierCompany;
        private String invoiceNumber;
    }

}
