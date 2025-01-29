package com.example.demo.controller.general;


import com.example.demo.base.ApiResponse;
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
@RequestMapping("api/permit/raffle")
    public class LikeController {

    private final LikeService likeService;

    //찜하기
    @PostMapping("/like")
    public ApiResponse<LikeResponseDTO> addLike(
             Long raffleId, Authentication authentication) {

        LikeResponseDTO likeResponse = likeService.addLike(raffleId,authentication);
        return ApiResponse.of(SuccessStatus._OK, likeResponse);
    }

    //찜 삭제
    @DeleteMapping("/like")
    public ApiResponse<String> deleteLike(
            Long raffleId,Authentication authentication) {

        likeService.deleteLike(raffleId, authentication);
        return ApiResponse.of(SuccessStatus._OK, null);
    }

    //찜 목록 조회
    @GetMapping("/like")
    public ApiResponse<List<LikeListResponseDTO>> getLikedItems(Authentication authentication) {

        List<LikeListResponseDTO> likeResponseList = likeService.getLikedItems(authentication);

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

