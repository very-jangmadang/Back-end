package com.example.demo.web.dto.Like;

import lombok.Getter;

@Getter
public class LikeListResponseDTO {

    private Long likeId;
    private Long raffleId;
    private String raffleName;
    private Long userId;

    // 생성자
    public LikeListResponseDTO(Long likeId, Long rappleId, String raffleName, Long userId) {
        this.likeId = likeId;
        this.raffleId = rappleId;
        this.raffleName = raffleName;
        this.userId = userId;
    }

    // Getter 메서드
    public Long getLikeId() {
        return likeId;
    }

    public Long getRaffleId() {
        return raffleId;
    }

    public String getRaffleName() {
        return raffleName;
    }

    public Long getUserId() {
        return userId;
    }
}
