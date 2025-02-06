package com.example.demo.entity;

import com.example.demo.entity.base.enums.ItemStatus;
import com.example.demo.entity.base.enums.RaffleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private ItemStatus itemStatus;

    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime startAt;

    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime endAt;

    private int ticketNum;

    private int minTicket;

    @Builder.Default
    private int view = 0; // 초기값 0

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isRedrawn = false;

    @Enumerated (EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15)")
    private RaffleStatus raffleStatus;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingFee;

    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL)
    List<Apply> applyList;

    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL)
    @Builder.Default // 이슈
    List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL)
    List<Delivery> delivery;

    // 연관관계 편의 메서드
    public void addImage(Image image) {
        this.images.add(image); //
        image.setRaffle(this);
    }

    public void addDelivery(Delivery delivery) {
        this.delivery.add(delivery);
    }

    // 조회수 증가
    public void addView() {
        this.view += 1;
    }

    public void setRaffleStatus(RaffleStatus raffleStatus) { this.raffleStatus = raffleStatus; }
    public void setWinner(User winner) { this.winner = winner; }
    public void setIsRedrawn() { this.isRedrawn = true; }


}
