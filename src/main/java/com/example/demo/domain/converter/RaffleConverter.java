package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Image;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.RaffleStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class RaffleConverter {

    public static Raffle toRaffle(RaffleRequestDTO.UploadDTO request, Category category, User user) {

        return Raffle.builder()
                .user(user)
                .winner(null)
                .category(category)
                .name(request.getName())
                .itemStatus(request.getItemStatus())
                .description(request.getDescription())
                .ticketNum(request.getTicketNum())
                .minTicket(request.getMinTicket())
                .startAt(request.getStartAt().withSecond(0).withNano(0))
                .endAt(request.getEndAt().withSecond(0).withNano(0))
                .raffleStatus(RaffleStatus.UNOPENED)
                .build();
    }

    public static RaffleResponseDTO.UploadResultDTO toUploadResultDTO(Raffle raffle) {
        return RaffleResponseDTO.UploadResultDTO.builder()
                .raffle_id(raffle.getId())
                .build();
    }

    public static RaffleResponseDTO.RaffleDetailDTO toDetailDTO(Raffle raffle, int likeCount, int applyCount, int followCount, int reviewCount) {

        return RaffleResponseDTO.RaffleDetailDTO.builder()
                .imageUrls(raffle.getImages().stream().map(Image::getImageUrl).toList()) // 이미지 url 리스트 (추후 쿼리 개선)
                .name(raffle.getName()) // 상품명
                .description(raffle.getDescription()) // 상품설명
                .category(raffle.getCategory().getName()) // 카테고리명
                .ticketNum(raffle.getTicketNum()) // 응모에 필요한 티켓 수
                .startAt(raffle.getStartAt()) // 응모 오픈
                .endAt(raffle.getEndAt()) // 응모 마감
                .view(raffle.getView()) // 조회 수
                .likeCount(likeCount) // 찜 수
                .applyCount(applyCount) // 응모 수
                .minUser(Math.round((float)raffle.getMinTicket() / raffle.getTicketNum())) // 판매자 희망 최소 참여자 수
                .nickname(raffle.getUser().getNickname()) // 판매자 닉네임
                .followCount(followCount) // 팔로우 수
                .reviewCount(reviewCount) // 리뷰 수
                .build();
    }
}
