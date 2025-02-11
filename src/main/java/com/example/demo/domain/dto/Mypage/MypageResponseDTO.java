package com.example.demo.domain.dto.Mypage;

import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MypageResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaffleDto {
        private Long raffleId;
        private String raffleName;
        private String raffleImage;
        private int ticketNum;
        private int applyNum;
        private Long timeUntilEnd;
        private boolean finished;
        private boolean liked;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyListDto {
        List<RaffleDto> raffleList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private Long addressId;
        private String addressName;
        private String recipientName;
        private String addressDetail;
        private String phoneNumber;
        private Boolean isDefault;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressListDto {
        List<AddressDto> addressList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageInfoDto {
        private String nickname;
        private int followerNum;
        private int reviewNum;
        List<RaffleDto> raffles;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageInfoWithReviewsDto{
        private String nickname;
        private int followerNum;
        private int reviewNum;
        private double avgScore;
        List<ReviewResponseDTO> reviews;
    }
  
}
