package com.example.demo.entity.Payment;

import com.example.demo.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserPayment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_payment_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String userId;

    @NotNull
    @Column(nullable = false)
    private String bankName;

    @NotNull
    @Column(nullable = false)
    private String bankNumber;

    @NotNull
    @Column(nullable = false)
    private int userTicket;
}