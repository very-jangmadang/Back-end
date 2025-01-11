package com.example.demo.web.dto.Like;

import lombok.Getter;

@Getter
public class LikeListResponseDTO {

    private Long likeId;
    private Long rappleId;
    private String rappleName;
    private Long userId;

    // 생성자
    public LikeListResponseDTO(Long likeId, Long rappleId, String productName, Long userId) {
        this.likeId = likeId;
        this.rappleId = rappleId;
        this.rappleName = rappleName;
        this.userId = userId;
    }

    // Getter 메서드
    public Long getLikeId() {
        return likeId;
    }

    public Long getRappleId() {
        return rappleId;
    }

    public String getRappleName() {
        return rappleName;
    }

    public Long getUserId() {
        return userId;
    }
}
