package com.example.demo.controller.general;


import com.example.demo.base.ApiResponse;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
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
@RequestMapping("/raffle")
    public class likecontroller {

    @Autowired
    private likeRepository likeRepository;


    //찜하기
    @Transactional
    @PostMapping("{raffleId}/like")
    public ApiResponse<LikeResponseDTO> addLike(
            @PathVariable Long raffleId,
            @RequestBody LikeRequestDTO likeRequest) {

        Long user = likeRequest.getUserId();

        LikeResponseDTO likeResponse = new LikeResponseDTO(1L, raffleId, likeRequest.getUserId());
        return new ApiResponse<>(true, "COMMON200", "성공입니다.", likeResponse);
    }

    //찜 삭제
    @Transactional
    @DeleteMapping("{raffleId}/like")
    public ApiResponse<String> deleteLike(
            @PathVariable Long raffleId,
            @RequestBody LikeRequestDTO likeRequest) {

        Long userId = likeRequest.getUserId();

        // 찜 내역 조회
        Like like = likeRepository.findByUserIdAndRaffleId(userId, raffleId)
                .orElseThrow(() -> new IllegalArgumentException("찜하지 않은 항목입니다."));

        // 찜 삭제
        likeRepository.delete(like);

        return new ApiResponse<>(true, "COMMON200", "찜이 삭제되었습니다.", null);
    }

    //찜 목록 조회
    @Transactional
    @GetMapping("/like")
    public ApiResponse<List<LikeListResponseDTO>> getLikedItems(@RequestParam Long userId) {

        List<Like> likes = likeRepository.findByUserId(userId);

        List<LikeListResponseDTO> likeResponseList = likes.stream()
                .map(like -> {
                    Raffle raffle = like.getRaffle();  // 찜한 Rapple 객체
                    return new LikeListResponseDTO(
                            like.getId(),            // likeId
                            raffle.getId(),          // raffleId
                            raffle.getName(),        // productName
                            like.getUser().getId()   // userId
                    );
                }).collect(Collectors.toList());


        return new ApiResponse<>(true, "COMMON200", "성공입니다.", likeResponseList);
    }


}

