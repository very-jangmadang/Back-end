package com.example.demo.domain.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long reviewId;
    private Long userId;
    private Long raffleId;
    private String raffleName;
    private String profileImageUrl;
    private Long reviewerId;
    private double score;
    private String text;
    private List<String> imageUrls;
    private LocalDateTime timestamp;

}
