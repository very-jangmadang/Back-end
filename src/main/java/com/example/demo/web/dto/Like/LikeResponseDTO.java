package com.example.demo.web.dto.Like;

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
    private Long rappleId;
    private Long userId;

}
