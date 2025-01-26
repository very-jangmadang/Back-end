package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(length = 100)
    private String addressName; // 배송지명

    @Column(length = 50)
    private String recipientName;  // 받는 사람

    @Column(length = 255)
    private String addressDetail;  // 주소

    @Column(length = 20)
    private String postalCode;  // 우편번호

    @Column(length = 20)
    private String phoneNumber;  // 연락처

    private boolean isDefault;  // 기본 배송지 여부

    @Column(length = 255)
    private String message;  // 배송 메세지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setDefaultAddress() {
        this.user.getAddresses().forEach(addr -> {
            if (addr != this)
                addr.isDefault = false;
        });

        this.isDefault = true;
    }

    public void setUser(User user) { this.user = user; }
    public void setMessage(String message) { this.message = message; }

}
