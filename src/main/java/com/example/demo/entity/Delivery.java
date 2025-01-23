package com.example.demo.entity;

import com.example.demo.entity.base.enums.CourierCompany;
import com.example.demo.entity.base.enums.DeliveryStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raffle_id")
    private Raffle raffle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated (EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(30)")
    private DeliveryStatus deliveryStatus;

    private LocalDateTime addressDeadline;

    private LocalDateTime shippingDeadline;

    private String invoiceNumber;       // 운송장 번호

    @Enumerated (EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private CourierCompany courierCompany;      // 택배사

    public void setAddress(Address address) { this.address = address; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public void setCourierCompany(CourierCompany courierCompany) { this.courierCompany = courierCompany; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setAddressDeadline(LocalDateTime addressDeadline) { this.addressDeadline = addressDeadline; }
    public void setShippingDeadline(LocalDateTime shippingDeadline) { this.shippingDeadline = shippingDeadline; }

}
