package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.MypageConverter;
import com.example.demo.domain.dto.MypageResponseDTO;
import com.example.demo.entity.Apply;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.ApplyRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;
    private final LikeRepository likeRepository;

    @Override
    public MypageResponseDTO.ApplyListDto getApplies() {

        // 사용자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        List<Apply> applyList = applyRepository.findByUser(user);

        List<MypageResponseDTO.RaffleDto> applyListDtos = new ArrayList<>();
        for (Apply apply : applyList) {
            Raffle raffle = apply.getRaffle();

            int applyNum = applyRepository.countByRaffle(raffle);
            boolean isLiked = likeRepository.existsByRaffleAndUser(raffle, user);

            MypageResponseDTO.RaffleDto raffleDto = MypageConverter.toRaffleDto(raffle, applyNum, isLiked);
            applyListDtos.add(raffleDto);
        }

        return MypageResponseDTO.ApplyListDto.builder()
                .raffleList(applyListDtos)
                .build();

    }

}
