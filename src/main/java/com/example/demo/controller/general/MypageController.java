package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.service.general.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
    public ApiResponse<String> uploadProfileImage(Authentication authentication, @RequestPart MultipartFile profile) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        // 프로필 이미지 업데이트
        Long userId = Long.parseLong(authentication.getName());
        String profileImageUrl = mypageService.updateProfileImage(userId, profile);

        return ApiResponse.of(SuccessStatus._OK, profileImageUrl);
    }

    //내 리뷰 조회
    @GetMapping("/review")
    public ApiResponse<ReviewWithAverageDTO> getMyReviewsByUserId(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        ReviewWithAverageDTO reviews = mypageService.getMyReviewsByUserId(userId);

        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

    @GetMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> getAddresses(){

        return ApiResponse.of(SuccessStatus._OK, mypageService.getAddresses());
    }

    @PostMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> setDefault(
            @RequestBody MypageRequestDTO.AddressDto addressDto){

        return ApiResponse.of(SuccessStatus._OK, mypageService.setDefault(addressDto));
    }

    @PostMapping("/setting/addresses/add")
    public ApiResponse<?> addAddress(
            @RequestBody MypageRequestDTO.AddressAddDto addressAddDto){

        mypageService.addAddress(addressAddDto);

        return ApiResponse.of(SuccessStatus._OK, null);
    }
}


