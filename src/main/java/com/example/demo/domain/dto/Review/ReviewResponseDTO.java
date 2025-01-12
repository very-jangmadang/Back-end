package com.example.demo.domain.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;      // 리뷰 ID
    private Long userId;        // 판매자 ID
    private Long reviewerId;    // 구매자 ID
    private Integer score;      // 점수
    private String text;        // 리뷰 내용
    private LocalDateTime timestamp;   // 작성 시간
}
