package com.example.demo.domain.dto.Like;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeListResponseDTO {

    private Long likeId;
    private Long raffleId;
    //private String raffleStatus;
    private int ticketNum;
    private String imageUrl;
    private Long timeUntilEnd;
    private String raffleName;
    private Long userId;


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
