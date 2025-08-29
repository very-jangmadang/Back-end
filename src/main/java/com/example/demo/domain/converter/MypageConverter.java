package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Mypage.MypageRequestDTO;
import com.example.demo.domain.dto.Mypage.MypageResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.base.enums.RaffleStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import com.example.demo.entity.Address;

public class MypageConverter {

    public static MypageResponseDTO.RaffleDto toRaffleDto(Raffle raffle, int applyNum, boolean isLiked) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = raffle.getEndAt();

        Duration duration = Duration.between(now, endAt);

        boolean isFinished = duration.isNegative();


        return MypageResponseDTO.RaffleDto.builder()
                .raffleId(raffle.getId())
                .raffleName(raffle.getName())
                .raffleImage(raffle.getImages().get(0).getImageUrl())
                .ticketNum(raffle.getTicketNum())
                .applyNum(applyNum)
                .status(raffle.getRaffleStatus())
                .timeUntilEnd(duration.toSeconds())
                .finished(isFinished)
                .liked(isLiked)
                .build();
    }

    public static MypageResponseDTO.AddressDto toAddressDto(Address address) {
        return MypageResponseDTO.AddressDto.builder()
                .addressId(address.getId())
                .addressName(address.getAddressName())
                .recipientName(address.getRecipientName())
                .addressDetail(address.getAddressDetail())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .build();
    }

    public static Address toAddress(MypageRequestDTO.AddressAddDto addressAddDto) {
        return Address.builder()
                .addressName(addressAddDto.getAddressName())
                .recipientName(addressAddDto.getRecipientName())
                .addressDetail(addressAddDto.getAddressDetail())
                .phoneNumber(addressAddDto.getPhoneNumber())
                .isDefault(addressAddDto.getIsDefault())
                .message(addressAddDto.getMessage())
                .build();
    }
  
}
