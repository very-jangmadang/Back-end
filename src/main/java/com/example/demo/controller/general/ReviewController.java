
package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.service.general.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Mod10Check;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/permit/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    //리뷰 작성
    @Operation(summary = "리뷰 작성")
    @PostMapping(value="/",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewResponseDTO> addReview(
            @ModelAttribute @Valid ReviewRequestDTO.ReviewUploadDTO reviewRequest,Authentication authentication) {

        ReviewResponseDTO reviewResponse = reviewService.addReview(reviewRequest,authentication);

        return ApiResponse.of(SuccessStatus._OK, reviewResponse);

    }

    //리뷰 삭제
    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<ReviewResponseDTO> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {

        reviewService.deleteReview(reviewId,authentication);

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    //상대 리뷰 조회
    @GetMapping("/{userId}/review")
    public ApiResponse<ReviewWithAverageDTO> getReviewsByUserId(@PathVariable Long userId) {

        ReviewWithAverageDTO reviews = reviewService.getReviewsByUserId(userId);

        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

}

