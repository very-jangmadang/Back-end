package com.example.demo.service.general.impl;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.ReviewConverter;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Review;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.general.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 작성
    public ReviewResponseDTO addReview(ReviewRequestDTO reviewRequest) {

        ReviewResponseDTO reviewResponse = ReviewConverter.ToReviewResponseDTO(reviewRequest);

        return reviewResponse;

    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, ReviewDeleteDTO reviewDelete) {

        // 리뷰 내역 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorStatus.REVIEW_NOT_FOUND));

        // 삭제
        reviewRepository.deleteById(reviewId);

    }
}
