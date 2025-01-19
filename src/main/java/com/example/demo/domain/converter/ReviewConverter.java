package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;

import java.time.LocalDateTime;

public class ReviewConverter {

    public static ReviewResponseDTO ToReviewResponseDTO(ReviewRequestDTO reviewRequest) {

        return ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(reviewRequest.getUserId())
                .reviewerId(reviewRequest.getReviewerId())
                .score(reviewRequest.getScore())
                .text(reviewRequest.getText())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
