package com.example.demo.service.general;

import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    // 리뷰 작성
    ReviewResponseDTO addReview(ReviewRequestDTO.ReviewUploadDTO reviewRequest,Long userId);

    // 리뷰 삭제
    void deleteReview(Long reviewId, Long userId);

    // 리뷰 조회
    ReviewWithAverageDTO getReviewsByUserId(Long userId);
}


