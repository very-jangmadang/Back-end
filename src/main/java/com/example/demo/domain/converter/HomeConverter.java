package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Home.HomeRaffleDTO;
import com.example.demo.domain.dto.Home.HomeRaffleListDTO;
import com.example.demo.domain.dto.base.PageInfo;
import com.example.demo.entity.Image;
import com.example.demo.entity.Raffle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class HomeConverter {

    public static HomeRaffleDTO toHomeRaffleDTO(Raffle raffle, boolean likeStatus){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = raffle.getEndAt();

        Duration duration = Duration.between(now, endAt);
        boolean finish = duration.isNegative();


        return HomeRaffleDTO.builder()
                .raffleId(raffle.getId())
                .imageUrls(raffle.getImages().stream().map(Image::getImageUrl).toList()) // 이미지 url 리스트 (추후 쿼리 개선)
                .like(likeStatus)
                .name(raffle.getName())
                .ticketNum(raffle.getTicketNum())
                .timeUntilEnd(duration.toSeconds())
                .participantNum(raffle.getApplyList().size())
                .raffleStatus(raffle.getRaffleStatus())
                .finish(finish)
                .build();
    }

    public static HomeRaffleListDTO toHomeRaffleListDTO(List<HomeRaffleDTO> raffleList, PageInfo pageInfo){
        return HomeRaffleListDTO.builder()
                .raffles(raffleList)
                .pageInfo(pageInfo)
                .build();
    }

}
