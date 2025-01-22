package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Like.LikeRequestDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class LikeConverter {


    public static LikeResponseDTO ToLikeResponseDTO(Like like) {
        return LikeResponseDTO.builder()
                .likeId(like.getId())
                .userId(like.getUser().getId())
                .raffleId(like.getRaffle().getId())
                .build();
    }
}
