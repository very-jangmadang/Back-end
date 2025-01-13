package com.example.demo.domain.converter;

import com.example.demo.domain.dto.RaffleRequestDTO;
import com.example.demo.domain.dto.RaffleResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

public class RaffleConverter {

    public static Raffle toRaffle(RaffleRequestDTO.UploadDTO request, Category category, User user) {

        return Raffle.builder()
                .user(user)
                .winner(null)
                .category(category)
                .name(request.getName())
                .status(request.getStatus())
                .description(request.getDescription())
                .ticketNum(request.getTicketNum())
                .minTicket(request.getMinTicket())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
//                .imageUrl() 일요일 이후 구현 예정
                .build();
    }

    public static RaffleResponseDTO.UploadResultDTO toUploadResultDTO(Raffle raffle) {
        return RaffleResponseDTO.UploadResultDTO.builder()
//                .imageUrl(raffle.getImageUrl())
                .title(raffle.getName())
                .ticketNum(raffle.getTicketNum())
                .build();
    }

    public static RaffleResponseDTO.RaffleDetailDTO toDetailDTO(Raffle raffle) {
        return RaffleResponseDTO.RaffleDetailDTO.builder()
                .name(raffle.getName())
                .category(raffle.getCategory().getName())
                .ticketNum(raffle.getTicketNum())
                .startAt(raffle.getStartAt())
                .endAt(raffle.getEndAt())
                .view(raffle.getView())
                .likeCount(raffle.getLikeCount())

                .minTicket(raffle.getMinTicket())
                // 현재 참여자 수 필요

                .nickname(raffle.getUser().getNickname())
                // 팔로우 수, 후기 수 필요
                .build();
    }
}
