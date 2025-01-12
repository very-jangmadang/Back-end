package com.example.demo.controller.general;


import com.example.demo.base.ApiResponse;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.service.general.LikeService;
import com.example.demo.web.dto.Like.LikeListResponseDTO;
import com.example.demo.web.dto.Like.LikeRequestDTO;
import com.example.demo.web.dto.Like.LikeResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/raffle")
    public class likecontroller {

    private final LikeService likeService;

    //찜하기
    @PostMapping("{raffleId}/like")
    public ApiResponse<LikeResponseDTO> addLike(
            @PathVariable Long raffleId,
            @RequestBody LikeRequestDTO likeRequest) {

        LikeResponseDTO likeResponse = likeService.addLike(raffleId, likeRequest);
        return new ApiResponse<>(true, "COMMON200", "성공입니다.", likeResponse);
    }

    //찜 삭제
    @DeleteMapping("{raffleId}/like")
    public ApiResponse<String> deleteLike(
            @PathVariable Long raffleId,
            @RequestBody LikeRequestDTO likeRequest) {

        Long userId = likeRequest.getUserId();

        likeService.deleteLike(raffleId, userId);

        return new ApiResponse<>(true, "COMMON200", "찜이 삭제되었습니다.", null);
    }

    //찜 목록 조회
    @GetMapping("/like")
    public ApiResponse<List<LikeListResponseDTO>> getLikedItems(@RequestParam Long userId) {

        List<LikeListResponseDTO> likeResponseList = likeService.getLikedItems(userId);

        return new ApiResponse<>(true, "COMMON200", "성공입니다.", likeResponseList);
    }

}

