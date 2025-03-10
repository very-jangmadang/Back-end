package com.example.demo.entity.Payment;

import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(nullable = false)
    private String orderId;

    @NotNull
    @Column(nullable = false)
    private String itemId;

    @NotNull
    @Column(nullable = false)
    private String itemName;

    @NotNull
    @Column(nullable = false)
    private int quantity;

    @NotNull
    @Column(nullable = false)
    private int amount;

    @NotNull
    @Column(nullable = false, unique = true)
    private String tid;

    @NotNull
    @Column(nullable = false)
    private String status;

    private LocalDateTime approvedAt;
}