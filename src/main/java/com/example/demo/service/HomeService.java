package com.example.demo.service;

import com.example.demo.domain.converter.RappleConverter;
import com.example.demo.domain.dto.HomeResponseDTO;
import com.example.demo.entity.Rapple;
import com.example.demo.repository.RappleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final RappleRepository rappleRepository;

    public HomeResponseDTO getHome(){

        HomeResponseDTO result = new HomeResponseDTO();
        List<Rapple> rapples = rappleRepository.findAll();


        // 마감임박인 래플 5개 조회
        LocalDateTime now = LocalDateTime.now();

        List<Rapple> rapplesSortedByEndAt = rapples.stream()
                .filter(r -> Duration.between(now, r.getEndAt()).toMillis() >= 0)
                .sorted(Comparator.comparingLong(r -> Duration.between(now, r.getEndAt()).toMillis()))
                .limit(5)
                .toList();

        for (Rapple rapple : rapplesSortedByEndAt) {
            HomeResponseDTO.RappleDTO rappleDTO = RappleConverter.toRappleDTO(rapple);
            result.getApproaching().add(rappleDTO);
        }

        // 응모자순으로 래플 조회
        Stream<Rapple> rapplesSortedByApplyList = rapples.stream()
                .sorted((r1, r2) -> Integer.compare(
                        r2.getApplyList() != null ? r2.getApplyList().size() : 0,
                        r1.getApplyList() != null ? r1.getApplyList().size() : 0
                ));

        for (Rapple rapple : rapplesSortedByEndAt) {
            HomeResponseDTO.RappleDTO rappleDTO = RappleConverter.toRappleDTO(rapple);
            result.getRapples().add(rappleDTO);
        }

        //TODO 내가 찜한 래플 조회 (로그인이 됐을 시)

        return result;
    }

}
