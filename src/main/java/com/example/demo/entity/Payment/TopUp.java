package com.example.demo.entity.Payment;

import com.example.demo.entity.base.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "topup", uniqueConstraints = @UniqueConstraint(name = "uk_txid", columnNames = "txId"))
@Getter @Setter
public class TopUp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false, length=120)
    private String txId;

    // 원본 값(wei 등) 그대로 저장(문자열 가능)
    @Column(nullable=false, length=64)
    private String rawValue;

    // 적립된 티켓 수 (정수)
    @Column(nullable=false)
    private Integer tickets;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private OrderStatus status;

    private String fromAddress;
    private String toAddress;

    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

}