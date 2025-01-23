package com.example.demo.entity;

import com.example.demo.entity.base.enums.InquiryStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Inquiry extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raffle_id")
    private Raffle raffle;

    @Column(length = 30)
    private String title;

    @Enumerated(EnumType.STRING)
    @Setter
    private InquiryStatus status;

    private String content;

}
