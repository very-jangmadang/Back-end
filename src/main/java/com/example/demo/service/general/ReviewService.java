package com.example.demo.service.general;


import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;

public interface ReviewService {

    // 리뷰 작성
    ReviewResponseDTO addReview(ReviewRequestDTO reviewRequest);

    // 리뷰 삭제
    void deleteReview(Long reviewId, ReviewDeleteDTO reviewDelete);

}

