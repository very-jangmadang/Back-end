package com.example.demo.service.general.impl;

import com.example.demo.domain.converter.HomeConverter;
import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final RaffleRepository raffleRepository;

    public HomeResponseDTO getHome(){

        List<Raffle> raffles = raffleRepository.findAll();


        // 마감임박인 래플 5개 조회
        LocalDateTime now = LocalDateTime.now();

        List<Raffle> rafflesSortedByEndAt = raffles.stream()
                .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                .limit(5)
                .toList();

        List<HomeResponseDTO.RaffleDTO> rafflesSortedByEndAtDTO = new ArrayList<>();

        for (Raffle raffle : rafflesSortedByEndAt) {
            HomeResponseDTO.RaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            rafflesSortedByEndAtDTO.add(raffleDTO);
        }

        // 응모자순으로 래플 조회 (응모 안마감된것 우선)
        List<Raffle> rafflesSortedByApplyList = Stream.concat(
                raffles.stream()
                        .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                        .sorted((r1, r2) -> Integer.compare(
                                r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                                r1.getApplyList() != null ? r1.getApplyList().size() : 0
                        )),
                raffles.stream()
                        .filter(r -> Duration.between(now, r.getEndAt()).toMillis() < 0)
                        .sorted((r1, r2) -> Integer.compare(
                                r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                                r1.getApplyList() != null ? r1.getApplyList().size() : 0
                        ))
        ).toList();

        List<HomeResponseDTO.RaffleDTO> rafflesSortedByApplyListDTO = new ArrayList<>();

        for (Raffle raffle : rafflesSortedByApplyList) {
            HomeResponseDTO.RaffleDTO raffleDTO = HomeConverter.toHomeRaffleDTO(raffle);
            rafflesSortedByApplyListDTO.add(raffleDTO);
        }

        return getHomeResponseDTO(rafflesSortedByEndAtDTO, null, rafflesSortedByApplyListDTO);
    }




    // HomeResponseDTO 만드는 메소드 분리
    private static HomeResponseDTO getHomeResponseDTO(List<HomeResponseDTO.RaffleDTO> rafflesSortedByEndAtDTO, List<HomeResponseDTO.RaffleDTO> myLikeRafflesDTO, List<HomeResponseDTO.RaffleDTO> rafflesSortedByApplyListDTO) {
        return HomeResponseDTO.builder()
                .approaching(rafflesSortedByEndAtDTO)
                .myLikeRaffles(myLikeRafflesDTO)
                .raffles(rafflesSortedByApplyListDTO)
                .build();
    }

}
