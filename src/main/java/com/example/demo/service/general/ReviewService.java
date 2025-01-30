package com.example.demo.service.general;

import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    // 리뷰 작성
    ReviewResponseDTO addReview(ReviewRequestDTO.ReviewUploadDTO reviewRequest);

    // 리뷰 삭제
    void deleteReview(Long reviewId, ReviewDeleteDTO reviewDelete);

    //리뷰 조회
    ReviewWithAverageDTO getReviewsByUserId(Long userId);

}


