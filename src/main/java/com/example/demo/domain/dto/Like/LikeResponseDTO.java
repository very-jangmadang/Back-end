package com.example.demo.domain.dto.Like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDTO {

    private Long likeId;
    private Long raffleId;
    private Long userId;

}
