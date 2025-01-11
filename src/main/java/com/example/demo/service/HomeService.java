package com.example.demo.service;

import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final RaffleRepository raffleRepository;

    public HomeResponseDTO getHome(){

        HomeResponseDTO result = new HomeResponseDTO();
        List<Raffle> rapples = raffleRepository.findAll();


        // 마감임박인 래플 5개 조회
        LocalDateTime now = LocalDateTime.now();

        List<Raffle> rafflesSortedByEndAt = rapples.stream()
                .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                .limit(5)
                .toList();

        for (Raffle raffle : rafflesSortedByEndAt) {
            HomeResponseDTO.RaffleDTO raffleDTO = RaffleConverter.toRaffleDTO(raffle);
            result.getApproaching().add(raffleDTO);
        }

        // 응모자순으로 래플 조회
        List<Raffle> rafflesSortedByApplyList = rapples.stream()
                .sorted((r1, r2) -> Integer.compare(
                        r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                        r1.getApplyList() != null ? r1.getApplyList().size() : 0
                ))
                .toList();


        for (Raffle raffle : rafflesSortedByApplyList) {
            HomeResponseDTO.RaffleDTO raffleDTO = RaffleConverter.toRaffleDTO(raffle);
            result.getRaffles().add(raffleDTO);
        }

        //TODO 내가 찜한 래플 조회 (로그인이 됐을 시)

        return result;
    }

}
