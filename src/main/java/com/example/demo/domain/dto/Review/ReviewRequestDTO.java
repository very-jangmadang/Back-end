package com.example.demo.domain.dto.Review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReviewRequestDTO {

    private Long userId;  //판매자 ID
    private Long reviewerId;    // 구매자 ID
    private Integer score;      // 점수 (1~5)
    private String text;        // 내용
}
