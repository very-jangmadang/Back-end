package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.base.enums.RaffleStatus;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class RaffleConverter {

    public static Raffle toRaffle(RaffleRequestDTO.UploadDTO request, Category category, User user, int minUser) {

        return Raffle.builder()
                .user(user)
                .winner(null)
                .category(category)
                .name(request.getName())
//                .itemStatus(request.getItemStatus())
                .description(request.getDescription())
                .ticketNum(request.getTicketNum())
                .minTicket(request.getMinTicket())
                .minUser(minUser)
                .startAt(request.getStartAt().withSecond(0).withNano(0))
                .endAt(request.getEndAt().withSecond(0).withNano(0))
                .raffleStatus(RaffleStatus.UNOPENED)
//                .shippingFee(BigDecimal.valueOf(request.getDeliveryFee()))
                .build();
    }

    public static RaffleResponseDTO.ResponseDTO toRaffleResponseDTO(Raffle raffle) {
        return RaffleResponseDTO.ResponseDTO.builder()
                .raffle_id(raffle.getId())
                .build();
    }

    public static RaffleResponseDTO.RaffleDetailDTO toDetailDTO(Raffle raffle, int likeCount, int applyCount, int followCount, int reviewCount, String state, String isWinner, RaffleStatus raffleStatus, Long deliveryId, boolean followStatus,boolean likeStatus) {

        return RaffleResponseDTO.RaffleDetailDTO.builder()
                .imageUrls(raffle.getImages().stream().map(Image::getImageUrl).toList()) // 이미지 url 리스트 (추후 쿼리 개선)
                .name(raffle.getName()) // 상품명
                .description(raffle.getDescription()) // 상품설명
                .category(raffle.getCategory().getName()) // 카테고리명
                .ticketNum(raffle.getTicketNum()) // 응모에 필요한 티켓 수
                .minTicket(raffle.getMinTicket())
                .startAt(raffle.getStartAt()) // 응모 오픈
                .endAt(raffle.getEndAt()) // 응모 마감
                .view(raffle.getView()) // 조회 수
                .likeCount(likeCount) // 찜 수
                .applyCount(applyCount) // 응모 수
                .minUser(raffle.getMinUser()) // 최소 참가 인원
                .nickname(raffle.getUser().getNickname()) // 판매자 닉네임
                .storeId(raffle.getUser().getId())
                .followCount(followCount) // 팔로우 수
                .reviewCount(reviewCount) // 리뷰 수
                .followStatus(followStatus) // 팔로우 상태
                .likeStatus(likeStatus) // 찜 상태
                .storeImageUrl(raffle.getUser().getProfileImageUrl()) // 상점 프로필 이미지
                .userStatus(state) // 사용자 응모 상태
                .isWinner(isWinner) // 당첨여부
                .raffleStatus(raffleStatus) // 래플 상태
                .deliveryId(deliveryId) // 배송 정보 아이디
                .build();
    }

    public static RaffleResponseDTO.ApplyDTO toApplyDto(Apply apply) {
        return RaffleResponseDTO.ApplyDTO.builder()
                .userId(apply.getUser().getId())
                .raffleId(apply.getRaffle().getId())
                .raffleImage(apply.getRaffle().getImages().get(0).getImageUrl())
                .endAt(apply.getRaffle().getEndAt())
                .build();
    }
}
