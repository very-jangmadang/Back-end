package com.example.demo.domain.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long reviewId;
    private Long userId;
    private Long reviewerId;
    private float score;
    private String text;
    private LocalDateTime timestamp;

}