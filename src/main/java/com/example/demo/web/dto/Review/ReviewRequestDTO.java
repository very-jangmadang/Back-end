package com.example.demo.web.dto.Review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ReviewRequestDTO {

    private Long userId;  //판매자 ID
    private Long reviewerId;    // 구매자 ID
    private Integer score;      // 점수 (1~5)
    private String text;        // 내용
}
