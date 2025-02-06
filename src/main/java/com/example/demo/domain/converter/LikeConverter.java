package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Like.LikeListResponseDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.domain.dto.Review.ReviewRequestDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Image;
import com.example.demo.entity.Like;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LikeConverter {

    public static LikeResponseDTO ToLikeResponseDTO(Like like) {
        return LikeResponseDTO.builder()
                .likeId(like.getId())
                .userId(like.getUser().getId())
                .raffleId(like.getRaffle().getId())
                .build();
    }

    public static LikeListResponseDTO toLikeListResponseDTO(Like like,int applyCount) {

        Raffle raffle = like.getRaffle();
        LocalDateTime endAt = raffle.getEndAt(); // raffle의 마감 시간
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now,endAt);
        // 남은 시간을 초 단위로 계산
        long timeUntilEnd = duration.toMillis() / 1000;


        return LikeListResponseDTO.builder()
                .likeId(like.getId())             // likeId
                .raffleId(like.getRaffle().getId())         // raffleId
                .raffleStatus(like.getRaffle().getRaffleStatus()) // raffleStatus
                .ticketNum(like.getRaffle().getTicketNum())    // ticketNum
                .imageUrls(raffle.getImages().stream().map(Image::getImageUrl).toList()) // 이미지 url 리스트 (추후 쿼리 개선)
                .timeUntilEnd(timeUntilEnd)           // timeUntilEnd (남은 시간)
                .raffleName(like.getRaffle().getName())         // raffleName
                .applyCount(applyCount)
                .build();
    }

}
