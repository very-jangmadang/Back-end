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

    @Setter
    private int ticket_num = 0;

    //TODO: 후순위 기능인 rank
    // private Rank rank;

    @Column(length = 20)
    private String provider;

    @Column(length = 20)
    private String role;

    private String address;

    private double score;

    private LocalDateTime withdrawTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes;

}
