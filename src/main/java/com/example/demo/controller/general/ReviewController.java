package com.example.demo.controller.general;
import com.example.demo.base.ApiResponse;
import com.example.demo.entity.Review;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    //리뷰 작성
    @Transactional
    @PostMapping("/")
    public ApiResponse<ReviewResponseDTO> addReview(
            @RequestBody ReviewRequestDTO reviewrequest) {

        Long userId = reviewrequest.getUserId();
        Long reviewerId = reviewrequest.getReviewerId();
        Integer score = reviewrequest.getScore();
        String text = reviewrequest.getText();

        LocalDateTime timestamp = LocalDateTime.now();

        ReviewResponseDTO reviewResponse = new ReviewResponseDTO(
                1L, userId, reviewerId, score, text, timestamp);

        return new ApiResponse<>(true, "COMMON200", "성공입니다.", reviewResponse);
    }

    //리뷰 삭제
    @Transactional
    @DeleteMapping("/{reviewId}")
    public ApiResponse<ReviewResponseDTO> deleteReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDeleteDTO reviewDelete) {
        {

            // 리뷰 내역 조회
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다"));

            // 리뷰 삭제
            reviewRepository.deleteById(reviewId);

            return new ApiResponse<>(true, "COMMON200", "리뷰가 삭제되었습니다.", null);
        }
    }
}
