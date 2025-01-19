package com.example.demo.domain.dto.Like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCountResponseDTO {
    private Long raffleId;
    private Long likeCount;
}
