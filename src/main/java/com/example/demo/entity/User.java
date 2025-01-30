package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String nickname;

    @Builder.Default
    private int ticket_num = 0;

    //TODO: 후순위 기능인 rank
    // private Rank rank;

    @Column(nullable = false)
    @Setter
    @Builder.Default
    private double averageScore = 0;

    @Setter
    @Builder.Default
    private int reviewCount = 0;

    @Column(length = 20)
    private String provider;

    @Column(length = 20)
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @Builder.Default
    private double score = 0;

    private LocalDateTime withdrawTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes;

    public void setTicket_num(int ticket_num) { this.ticket_num = ticket_num; }

}
