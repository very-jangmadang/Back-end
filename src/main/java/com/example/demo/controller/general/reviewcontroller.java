package com.example.demo.controller.general;
import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class reviewcontroller {

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
                1L, userId,reviewerId,score,text,timestamp);

        return new ApiResponse<>(true, "COMMON200", "성공입니다.", reviewResponse);
    }
}
