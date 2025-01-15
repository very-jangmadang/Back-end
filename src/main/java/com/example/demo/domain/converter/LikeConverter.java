package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Like.LikeRequestDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;

import java.time.LocalDateTime;

public class LikeConverter {
    public static LikeResponseDTO ToLikeResponseDTO(LikeRequestDTO likeRequest,Long raffleId) {

        return LikeResponseDTO.builder()
                .likeId(1L)
                .userId(likeRequest.getUserId())
                .raffleId(raffleId)
                .build();
    }
}
