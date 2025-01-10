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
public class Refund extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime exchangeDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal exchangeAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(length = 50)
    private String depositAccountType;

    @Column(length = 50)
    private String depositAccountNumber;

}
