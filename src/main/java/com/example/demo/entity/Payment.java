package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime paymentDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(length = 50)
    private String paymentAccountType;

    @Column(length = 50)
    private String paymentAccountNumber;


}
