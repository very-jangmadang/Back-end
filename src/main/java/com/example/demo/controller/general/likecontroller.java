package com.example.demo.controller.general;


import com.example.demo.base.ApiResponse;
import com.example.demo.entity.Like;
import com.example.demo.entity.Rapple;
import com.example.demo.web.dto.Like.LikeListResponseDTO;
import com.example.demo.web.dto.Like.LikeRequestDTO;
import com.example.demo.web.dto.Like.LikeResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.demo.repository.likeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rapple")
    public class likecontroller {

    @Autowired
    private likeRepository likeRepository;


    //찜하기
    @Transactional
    @PostMapping("{rappleId}/like")
    public ApiResponse<LikeResponseDTO> addLike(
            @PathVariable Long rappleId,
            @RequestBody LikeRequestDTO likeRequest) {

        Long user = likeRequest.getUserId();

        LikeResponseDTO likeResponse = new LikeResponseDTO(1L, rappleId, likeRequest.getUserId());
        return new ApiResponse<>(true, "COMMON200", "성공입니다.", likeResponse);
    }

    //찜 삭제
    @Transactional
    @DeleteMapping("{rappleId}/like")
    public ApiResponse<String> deleteLike(
            @PathVariable Long rappleId,
            @RequestBody LikeRequestDTO likeRequest) {

        Long userId = likeRequest.getUserId();

        // userId와 rappleId로 찜 항목 삭제
        likeRepository.deleteByUserIdAndRappleId(userId, rappleId);

        return new ApiResponse<>(true, "COMMON200", "찜이 삭제되었습니다.", null);
    }

    //찜 목록 조회
    @Transactional
    @GetMapping("/like")
    public ApiResponse<List<LikeListResponseDTO>> getLikedItems(@RequestParam Long userId) {

        List<Like> likes = likeRepository.findByUserId(userId);

        List<LikeListResponseDTO> likeResponseList = likes.stream()
                .map(like -> {
                    Rapple rapple = like.getRapple();  // 찜한 Rapple 객체
                    return new LikeListResponseDTO(
                            like.getId(),            // likeId
                            rapple.getId(),          // rappleId
                            rapple.getName(),        // productName
                            like.getUser().getId()   // userId
                    );
                }).collect(Collectors.toList());


        return new ApiResponse<>(true, "COMMON200", "성공입니다.", likeResponseList);
    }


}

