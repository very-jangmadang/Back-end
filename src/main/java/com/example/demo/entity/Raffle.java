package com.example.demo.entity;

import com.example.demo.entity.base.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Raffle extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raffle_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 30)
    private String name;

    private String description;

    private String imageUrl;

    @Enumerated (EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Status status;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private int ticketNum;

    private int minTicket;

    private int likeCount = 0; // 초기값 0
  
    private int view = 0; // 초기값 0
  
    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL)
    List<Apply> applyList;


}
