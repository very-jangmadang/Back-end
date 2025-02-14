
package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.service.general.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    //리뷰 작성
    @Operation(summary = "리뷰 작성")
    @PostMapping(value="api/member/review",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewResponseDTO> addReview(
            @ModelAttribute @Valid ReviewRequestDTO.ReviewUploadDTO reviewRequest,Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        Long userId = Long.parseLong(authentication.getName());

        ReviewResponseDTO reviewResponse = reviewService.addReview(reviewRequest,userId);

        return ApiResponse.of(SuccessStatus._OK, reviewResponse);

    }

    //리뷰 삭제
    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("api/member/review")
    public ApiResponse<Void> deleteReview(
            @RequestParam Long reviewId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        reviewService.deleteReview(reviewId,userId);

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    //상대 리뷰 조회
    @GetMapping("api/permit/review/{userId}")
    public ApiResponse<ReviewWithAverageDTO> getReviewsByUserId(@PathVariable Long userId) {

        ReviewWithAverageDTO reviews = reviewService.getReviewsByUserId(userId);

        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

}

