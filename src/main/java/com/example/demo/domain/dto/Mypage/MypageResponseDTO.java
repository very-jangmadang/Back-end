package com.example.demo.domain.dto.Mypage;

import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.base.enums.RaffleStatus;
import io.micrometer.common.lang.Nullable;
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
        private RaffleStatus status;
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
        private String profileImageUrl;
        List<RaffleDto> raffles;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfoDto {
        private String nickname;
        private int followerNum;
        private int reviewNum;
        private String profileImageUrl;
        private boolean followStatus;
        List<RaffleDto> raffles;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfoWithReviewsDto{
        private String nickname;
        private int followerNum;
        private int reviewNum;
        private String profileImageUrl;
        private boolean followStatus;
        private double avgScore;
        List<ReviewResponseDTO> reviews;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckBusinessDto {
        private String nickname;
        private boolean isBusiness;
    }
  
}
