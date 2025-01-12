package com.example.demo.service.general.impl;

import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.RaffleRequestDTO;
import com.example.demo.domain.dto.RaffleResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.RaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RaffleServiceImpl implements RaffleService {

    private final RaffleRepository raffleRepository;

    @Override
    @Transactional
    public Raffle uploadRaffle(RaffleRequestDTO.UploadDTO request, User user) {

        // 요청받은 RaffleRequestDTO를 Raffle엔티키로 변환
        Raffle raffle = RaffleConverter.toRaffle(request, user);

        return raffleRepository.save(raffle);
    }

    @Override
    public Raffle getRaffleDetails(Long id) {
        return raffleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당하는 래플이 존재하지 않습니다 :" + id));
    }
}
