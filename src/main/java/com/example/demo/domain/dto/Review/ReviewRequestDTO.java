package com.example.demo.domain.dto.Review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReviewRequestDTO{
    private Long userId;
    private Long reviewerId;
    private float score;
    private String text;
}
