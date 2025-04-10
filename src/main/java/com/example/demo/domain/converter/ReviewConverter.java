package com.example.demo.domain.converter;


import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewConverter {

    public static Review toReview(ReviewRequestDTO.ReviewUploadDTO Reviewrequest, User user,Raffle raffle,User reviewer, List<String> imageUrls) {

        return Review.builder()
                .raffle(raffle)
                .user(user)
                .reviewer(reviewer)
                .score(Reviewrequest.getScore())
                .text(Reviewrequest.getText())
                .imageUrls(imageUrls)
                .build();
    }

    public static ReviewResponseDTO ToReviewResponseDTO(Review review) {
        boolean isRaffleDeleted = (review.getRaffle() == null);

        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .raffleId(isRaffleDeleted ? null: review.getRaffle().getId())
                .raffleName(isRaffleDeleted ? "삭제된 래플입니다." : review.getRaffle().getName())
                .reviewerId(review.getReviewer().getId())
                .profileImageUrl(review.getReviewer().getProfileImageUrl())
                .score((float) review.getScore())
                .text(review.getText())
                .imageUrls(review.getImageUrls())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static List<ReviewResponseDTO> toReviewResponseDTOList(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewConverter::ToReviewResponseDTO) // 개별 Review를 DTO로 변환
                .collect(Collectors.toList());
    }
}

