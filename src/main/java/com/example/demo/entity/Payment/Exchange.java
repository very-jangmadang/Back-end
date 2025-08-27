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
public class Exchange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private int amount; // 환전 금액

    private int quantity; // 환전 수량

    @NotNull
    private String exchangeMethod; // 환전 수단 (현재는 "통장 입금" 으로 고정)

    @NotNull
    @Column(nullable = false)
    private LocalDateTime exchangedAt; // 환전 일자

}