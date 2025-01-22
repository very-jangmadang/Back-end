package com.example.demo.domain.converter;

import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Address;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.util.List;

public class DrawConverter {

    public static DrawResponseDTO.DrawDto toDrawDto(Raffle raffle, List<String> nicknameList) {
        return DrawResponseDTO.DrawDto.builder()
                .raffleId(raffle.getId())
                .nicknameList(nicknameList)
                .build();
    }

    public static DrawResponseDTO.WinnerDto toWinnerDto(Raffle raffle) {
        return DrawResponseDTO.WinnerDto.builder()
                .raffleId(raffle.getId())
                .winnerId(raffle.getWinner().getId())
                .winnerNickname(raffle.getWinner().getNickname())
                .build();
    }

    public static DrawResponseDTO.AddressDto toAddressDto(Address address) {
        return DrawResponseDTO.AddressDto.builder()
                .addressName(address.getAddressName())
                .addressDetail(address.getAddressDetail())
                .isDefault(address.isDefault())
                .build();
    }

    public static DrawResponseDTO.DeliveryDto toDeliveryDto
            (Raffle raffle, User user, List<DrawResponseDTO.AddressDto> addressList) {
        return DrawResponseDTO.DeliveryDto.builder()
                .raffleId(raffle.getId())
                .winnerId(user.getId())
                .addressList(addressList)
                .build();
    }
}
