package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.DrawResponseDTO;
import com.example.demo.entity.Address;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.domain.converter.DrawConverter.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final ApplyRepository applyRepository;
    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;

    @Override
    public DrawResponseDTO.DrawDto getDrawRaffle(Long raffleId) {

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        List<Apply> applyList = applyRepository.findByRaffle(raffle);

        if (applyList.isEmpty())
            throw new CustomException(ErrorStatus.DRAW_EMPTY);

        List<String> nicknameList = applyList.stream()
                .map(apply -> apply.getUser().getNickname())
                .collect(Collectors.toList());

        return toDrawDto(raffle, nicknameList);

    }

    @Override
    public DrawResponseDTO.WinnerDto getWinner(Long raffleId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        return toWinnerDto(raffle);

    }

    @Override
    public DrawResponseDTO.DeliveryDto getDelivery(Long raffleId) {

        // 사용자 정보 조회 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        User winner = raffle.getWinner();

        if (user != winner)
            throw new CustomException(ErrorStatus.DRAW_FAIL);

        List<Address> addressList = user.getAddresses();

        if (addressList.isEmpty())
            throw new CustomException(ErrorStatus.DELIVERY_NO_ADDRESS);

        List<DrawResponseDTO.AddressDto> addressDtos = new ArrayList<>();
        for (Address address : addressList) {
            DrawResponseDTO.AddressDto addressDto = toAddressDto(address);
            addressDtos.add(addressDto);
        }

        return toDeliveryDto(raffle, user, addressDtos);
    }

}
