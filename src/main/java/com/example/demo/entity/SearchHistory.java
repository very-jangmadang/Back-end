package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchHistory extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String keyword;

    private int searchCount;

    private LocalDateTime searchedAt;

    /**
     * 연관관계 편의 메소드
     */

    // 검색기록을 최신화
    public void updateSearchHistory(){
        this.searchCount += 1;
        this.searchedAt = LocalDateTime.now();
    }


}
