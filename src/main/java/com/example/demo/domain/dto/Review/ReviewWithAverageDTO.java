package com.example.demo.domain.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewWithAverageDTO {
    private List<ReviewResponseDTO> reviews;
    private double averageScore;
    private int reviewCount;

}
