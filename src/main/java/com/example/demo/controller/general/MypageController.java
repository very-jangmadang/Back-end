package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.service.general.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/permit/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/applies")
    public ApiResponse<MypageResponseDTO.ApplyListDto> getApplies(){

        return ApiResponse.of(SuccessStatus._OK, mypageService.getApplies());

    }

    @PatchMapping(value="/profile-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadProfileImage(Long userId, @RequestPart MultipartFile profile) {

        // 프로필 이미지 업데이트
        String profileImageUrl = mypageService.updateProfileImage(userId, profile);

        return ApiResponse.of(SuccessStatus._OK, profileImageUrl);
    }

    //내 리뷰 조회
    @GetMapping("/{userId}/review")
    public ApiResponse<ReviewWithAverageDTO> getReviewsByUserId(@PathVariable Long userId) {

        ReviewWithAverageDTO reviews = mypageService.getReviewsByUserId(userId);

        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

}
