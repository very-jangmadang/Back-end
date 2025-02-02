package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.domain.dto.Review.ReviewWithAverageDTO;
import com.example.demo.service.general.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequestMapping("/api/permit/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/applies")
    public ApiResponse<MypageResponseDTO.ApplyListDto> getApplies(){

        return ApiResponse.of(_OK, mypageService.getApplies());

    }

    @Operation(summary = "프로필 이미지 변경하기")
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

    @Operation(summary = "닉네임 변경하기")
    @PatchMapping("/nickname")
    public ApiResponse<String> changeNickname(Authentication authentication, @RequestParam String nickname) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        String updatedNickname = mypageService.changeNickname(userId, nickname);

        return ApiResponse.of(SuccessStatus._OK, updatedNickname);
    }


    //내 리뷰 조회
    @Operation(summary = "내 리뷰 조회하기")
    @GetMapping("/review")
    public ApiResponse<ReviewWithAverageDTO> getMyReviewsByUserId(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        ReviewWithAverageDTO reviews = mypageService.getMyReviewsByUserId(userId);

        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

    @Operation(summary = "등록된 주소 조회하기")
    @GetMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> getAddresses(){

        return ApiResponse.of(_OK, mypageService.getAddresses());
    }

    @Operation(summary = "기본 배송지 설정하기")
    @PostMapping("/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> setDefault(
            @RequestBody MypageRequestDTO.AddressDto addressDto){

        return ApiResponse.of(_OK, mypageService.setDefault(addressDto));
    }

    @Operation(summary = "주소 추가하기")
    @PostMapping("/setting/addresses/add")
    public ApiResponse<?> addAddress(
            @RequestBody MypageRequestDTO.AddressAddDto addressAddDto){

        mypageService.addAddress(addressAddDto);

        return ApiResponse.of(_OK, null);
    }

    @Operation(summary = "주소 삭제하기")
    @DeleteMapping("/setting/addresses")
    public ApiResponse<?> deleteAddress(
            @RequestBody MypageRequestDTO.AddressDto addressDto){

        mypageService.deleteAddress(addressDto);

        return ApiResponse.of(_OK, null);
    }
}


