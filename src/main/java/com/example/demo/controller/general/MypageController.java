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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.base.status.SuccessStatus._OK;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @Operation(summary = "마이페이지 프로필(내가 응모한 래플 조회)")
    @GetMapping("/api/member/mypage")
    public ApiResponse<MypageResponseDTO.MyPageInfoDto> getMyPageMyApply(Authentication authentication){
        Long userId = Long.parseLong(authentication.getName());
        MypageResponseDTO.MyPageInfoDto result = mypageService.getMyPageMyApplyRaffles(userId);
        return ApiResponse.of(_OK, result);
    }

    @Operation(summary = "마이페이지 프로필(내가 주최한 래플 조회)")
    @GetMapping("/api/member/mypage/myRaffles")
    public ApiResponse<MypageResponseDTO.MyPageInfoDto> getMyPageMyHost(Authentication authentication){
        Long userId = Long.parseLong(authentication.getName());
        MypageResponseDTO.MyPageInfoDto result = mypageService.getMyPageMyHostRaffles(userId);
        return ApiResponse.of(_OK, result);
    }

    @Operation(summary = "상대 프로필 조회(상대방이 주최한 래플 조회)")
    @GetMapping("/api/permit/mypage/{userId}/myRaffles")
    public ApiResponse<MypageResponseDTO.ProfileInfoDto> getProfileHostRaffles(@PathVariable("userId") Long userId){
        MypageResponseDTO.ProfileInfoDto result = mypageService.getProfileHostRaffles(userId);
        return ApiResponse.of(_OK, result);
    }

    @Operation(summary = "상대 프로필 조회(상점 후기 조회)")
    @GetMapping("/api/permit/mypage/{userId}")
    public ApiResponse<MypageResponseDTO.ProfileInfoWithReviewsDto> getProfileReviews(@PathVariable("userId") Long userId) {
        MypageResponseDTO.ProfileInfoWithReviewsDto reviews = mypageService.getProfileReviews(userId);
        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

    @Transactional
    @Operation(summary = "내 팔로워 수 공개/비공개 설정")
    @PatchMapping("/api/member/mypage/secretInfo")
    public ApiResponse<Boolean> secretInfo(Authentication authentication, @RequestBody Boolean isVisible) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        Long userId = Long.parseLong(authentication.getName());

        boolean updatedVisibility = mypageService.updateFollowerVisibility(userId, isVisible);

        return ApiResponse.of(SuccessStatus._OK, updatedVisibility);
    }

    @Operation(summary = "내 팔로워 수 공개/비공개 설정")
    @GetMapping("/api/member/mypage/secretInfo")
    public ApiResponse<Boolean> getsecretInfo(Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        Long userId = Long.parseLong(authentication.getName());

        boolean Visibility = mypageService.getFollowerVisibility(userId);

        return ApiResponse.of(SuccessStatus._OK,Visibility);
    }


    @Operation(summary = "프로필 이미지 변경하기")
    @PatchMapping(value="/api/member/mypage/profile-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadProfileImage(Authentication authentication, @RequestPart MultipartFile profile) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        // 프로필 이미지 업데이트
        Long userId = Long.parseLong(authentication.getName());
        String profileImageUrl = mypageService.updateProfileImage(userId, profile);

        return ApiResponse.of(SuccessStatus._OK, profileImageUrl);
    }

    // 닉네임 변경
    @Operation(summary = "닉네임 변경하기")
    @PatchMapping("/api/member/mypage/nickname")
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
    @GetMapping("/api/member/mypage/review")
    public ApiResponse<ReviewWithAverageDTO> getMyReviewsByUserId(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        ReviewWithAverageDTO reviews = mypageService.getMyReviewsByUserId(userId);

        return ApiResponse.of(SuccessStatus._OK, reviews);
    }

    @Operation(summary = "등록된 주소 조회하기")
    @GetMapping("/api/member/mypage/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> getAddresses(){

        return ApiResponse.of(_OK, mypageService.getAddresses());
    }

    @Operation(summary = "기본 배송지 설정하기")
    @PostMapping("/api/member/mypage/setting/addresses")
    public ApiResponse<MypageResponseDTO.AddressListDto> setDefault(
            @RequestBody MypageRequestDTO.AddressDto addressDto){

        return ApiResponse.of(_OK, mypageService.setDefault(addressDto));
    }

    @Operation(summary = "주소 추가하기")
    @PostMapping("/api/member/mypage/setting/addresses/add")
    public ApiResponse<?> addAddress(
            @RequestBody MypageRequestDTO.AddressAddDto addressAddDto){

        mypageService.addAddress(addressAddDto);

        return ApiResponse.of(_OK, null);
    }

    @Operation(summary = "주소 삭제하기")
    @DeleteMapping("/api/member/mypage/setting/addresses")
    public ApiResponse<?> deleteAddress(
            @RequestBody MypageRequestDTO.AddressDeleteDto addressDeleteDto){

        mypageService.deleteAddress(addressDeleteDto);

        return ApiResponse.of(_OK, null);
    }

    @Operation(summary = "사업자 여부 확인")
    @GetMapping("/api/member/mypage/business")
    public ApiResponse<MypageResponseDTO.CheckBusinessDto> isBusiness(){
        return ApiResponse.of(_OK, mypageService.checkBusiness());
    }
}


