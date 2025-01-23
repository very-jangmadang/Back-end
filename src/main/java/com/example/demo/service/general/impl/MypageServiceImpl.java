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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        List<Apply> applyList = applyRepository.findWithRaffleByUser(user);
        applyList.sort(Comparator.comparing(Apply::getCreatedAt, Comparator.reverseOrder()));

        List<Long> raffleIds = applyList.stream()
                .map(apply -> apply.getRaffle().getId())
                .collect(Collectors.toList());

        List<Object[]> applyCounts = applyRepository.countAppliesByRaffleIds(raffleIds);
        List<Object[]> likeStatuses = likeRepository.checkLikesByRaffleIdsAndUser(raffleIds, user);

        Map<Long, Integer> raffleApplyCountMap = applyCounts.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> ((Long) result[1]).intValue()));

        Map<Long, Boolean> raffleLikeMap = likeStatuses.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (Boolean) result[1]));

        List<MypageResponseDTO.RaffleDto> applyListDtos = applyList.stream()
                .map(apply -> {
                    Raffle raffle = apply.getRaffle();
                    int applyNum = raffleApplyCountMap.getOrDefault(raffle.getId(), 0);
                    boolean isLiked = raffleLikeMap.getOrDefault(raffle.getId(), false);

                    return MypageConverter.toRaffleDto(raffle, applyNum, isLiked);
                })
                .collect(Collectors.toList());

        return MypageResponseDTO.ApplyListDto.builder()
                .raffleList(applyListDtos)
                .build();

    }

}
