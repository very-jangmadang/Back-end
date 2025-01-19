package com.example.demo.controller.general;
import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Review.ReviewDeleteDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.service.general.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/permit/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 작성
    @Operation(summary = "리뷰 작성")
    @PostMapping("/")
    public ApiResponse<ReviewResponseDTO> addReview(
            @RequestBody ReviewRequestDTO reviewRequest) {

        ReviewResponseDTO reviewResponse = reviewService.addReview(reviewRequest);

        return ApiResponse.of(SuccessStatus._OK, reviewResponse);

    }

    //리뷰 삭제
    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<ReviewResponseDTO> deleteReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDeleteDTO reviewDelete) {

        reviewService.deleteReview(reviewId,reviewDelete);

        return ApiResponse.of(SuccessStatus._OK, null);
    }
}
