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
    private int applyCount;


    // Getter 메서드
    public Long getRaffleId() {
        return raffleId;
    }

}
