package com.example.demo.domain.dto.Like;

import com.example.demo.entity.base.enums.RaffleStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeListResponseDTO {

    private Long likeId;
    private Long raffleId;
    private RaffleStatus raffleStatus;
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
