package com.example.demo.domain.dto.Like;

import com.example.demo.entity.base.enums.RaffleStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LikeListResponseDTO {

    private Long likeId;
    private Long raffleId;
    private RaffleStatus raffleStatus;
    private int ticketNum;
    private List<String> imageUrls;
    private Long timeUntilEnd;
    private String raffleName;
    private int applyCount;


    // Getter 메서드
    public Long getRaffleId() {
        return raffleId;
    }

}
