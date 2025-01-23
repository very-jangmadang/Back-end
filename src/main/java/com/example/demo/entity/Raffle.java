package com.example.demo.entity;

import com.example.demo.entity.base.enums.ItemStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Raffle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raffle_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 30)
    private String name;

    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private ItemStatus itemStatus;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private int ticketNum;

    private int minTicket;

    private int view = 0; // 초기값 0

//    private int likeCount = 0; // 초기값 0
//
//    private int apply_count = 0; // 초기값 0

    @Setter
    @Enumerated (EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15)")
    private RaffleStatus raffleStatus;

    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL)
    List<Apply> applyList;

    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL)
    @Builder.Default
    List<Image> images = new ArrayList<>();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    // 연관관계 편의 메서드
    public void addImage(Image image) {
        this.images.add(image); //
        image.setRaffle(this);
    }

    // 조회수 증가
    public void addView() {
        this.view += 1;
    }

//    // 찜 횟수 증가
//    public void upLikeCount(){
//        this.likeCount += 1;
//    }
//
//    // 찜 횟수 감소
//    public void downLikeCount(){
//        this.likeCount -= 1;
//    }
//
//    // 응모 수 증가
//    public void addApplyCount(){
//        this.apply_count += 1;
//    }
//
//    // 응모 수 감소
//    public void downApplyCount(){
//        this.apply_count -= 1;
//    }
}
