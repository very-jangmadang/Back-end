package com.example.demo.entity;

import com.example.demo.base.Constants;
import com.example.demo.entity.base.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raffle_id")
    private Raffle raffle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @Enumerated (EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(30)")
    private DeliveryStatus deliveryStatus;

    private LocalDateTime addressDeadline;

    private LocalDateTime shippingDeadline;

    private String invoiceNumber;       // 운송장 번호

    private boolean isAddressExtended;      // 배송지 입력 기한 연장 여부

    private boolean isShippingExtended;     // 운송장 입력 기한 연장 여부

    public void setAddress(Address address) { this.address = address; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public void setCourier(Courier courier) { this.courier = courier; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public void setAddressDeadline() {
        this.addressDeadline = LocalDateTime.now()
                .plusHours(Constants.ADDRESS_DEADLINE)
                .withSecond(0)
                .withNano(0);
    }

    public void setShippingDeadline() {
        this.shippingDeadline = LocalDateTime.now()
                .plusHours(Constants.SHIPPING_DEADLINE)
                .withSecond(0)
                .withNano(0);
    }

    public void extendAddressDeadline() {
        this.deliveryStatus = DeliveryStatus.WAITING_ADDRESS;
        this.addressDeadline = this.addressDeadline.plusHours(Constants.EXTENSION_HOURS);
        this.isAddressExtended = true;
    }

    public void extendShippingDeadline() {
        this.deliveryStatus = DeliveryStatus.READY;
        this.shippingDeadline = this.shippingDeadline.plusHours(Constants.EXTENSION_HOURS);
        this.isShippingExtended = true;
    }

}
