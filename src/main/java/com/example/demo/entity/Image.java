package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "raffle_id")
    private Raffle raffle;


    private String ImageUrl;

    @Column(name = "image_order")
    private int order; // 업로드 이미지 순서

    //연관관계 편의 메서드
    public void setRaffle(Raffle raffle) {
        this.raffle = raffle;
    }


}
