package com.example.demo.controller.general;


import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Like.LikeCountResponseDTO;
import com.example.demo.service.general.LikeService;
import com.example.demo.domain.dto.Like.LikeListResponseDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member/raffles")
    public class LikeController {

    private final LikeService likeService;

    //찜하기
    @PostMapping("/like")
    public ApiResponse<LikeResponseDTO> addLike(
             @RequestParam Long raffleId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        Long userId = Long.parseLong(authentication.getName());

        LikeResponseDTO likeResponse = likeService.addLike(raffleId,userId);
        return ApiResponse.of(SuccessStatus._OK, likeResponse);
    }

    //찜 삭제
    @DeleteMapping("/like")
    public ApiResponse<String> deleteLike(
            @RequestParam Long raffleId,Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }

        Long userId = Long.parseLong(authentication.getName());
        likeService.deleteLike(raffleId, userId);
        return ApiResponse.of(SuccessStatus._OK, null);
    }

    //찜 목록 조회
    @GetMapping("/like")
    public ApiResponse<List<LikeListResponseDTO>> getLikedItems(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure(ErrorStatus.COMMON_UNAUTHORIZED, null);
        }
        Long userId = Long.parseLong(authentication.getName());
        List<LikeListResponseDTO> likeResponseList = likeService.getLikedItems(userId);

        return ApiResponse.of(SuccessStatus._OK, likeResponseList);
    }

    // 찜 갯수 조회
    @GetMapping("{raffleId}/likeCount")
    public ApiResponse<LikeCountResponseDTO> getLikeCount(@PathVariable Long raffleId) {
        Long likeCount = likeService.getLikeCount(raffleId);
        LikeCountResponseDTO likeCountResponse = new LikeCountResponseDTO(raffleId, likeCount);

        return ApiResponse.of(SuccessStatus._OK, likeCountResponse);
    }

}

