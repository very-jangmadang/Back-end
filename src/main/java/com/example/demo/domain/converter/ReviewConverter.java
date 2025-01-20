package com.example.demo.domain.converter;


import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewConverter {

    public static Review toReview(ReviewRequestDTO.ReviewUploadDTO Reviewrequest, User user,User reviewer, List<String> imageUrls) {

        return Review.builder()
                .user(user)
                .reviewer(reviewer)
                .score(Reviewrequest.getScore())
                .text(Reviewrequest.getText())
                .imageUrls(imageUrls)
                .build();
    }

    public static ReviewResponseDTO ToReviewResponseDTO(Review review) {

        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .reviewerId(review.getReviewer().getId())
                .score((float) review.getScore())
                .text(review.getText())
                .imageUrls(review.getImageUrls())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

