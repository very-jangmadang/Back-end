package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.RaffleRequestDTO;
import com.example.demo.domain.dto.RaffleResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.RaffleSchedulerService;
import com.example.demo.service.general.RaffleService;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RaffleServiceImpl implements RaffleService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RaffleSchedulerService raffleSchedulerService;
    private final S3UploadService s3UploadService;

    @Override
    @Transactional
    public RaffleResponseDTO.UploadResultDTO uploadRaffle(RaffleRequestDTO.UploadDTO request) {

        log.info("uploadRaffle called with request: {}", request);

        // 0. 업로드 작성자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        log.info("User found: {}", user.getNickname());


        // 1. 요청받은 카테고리 이름으로 Category 엔티티 가져오기
        log.info("Request data: {}", request);
        log.info("Searching for category with name: {}", request.getCategory());
        log.info("All fields: file={}, category={}, name={}, status={}, description={}, ticketNum={}, minTicket={}, startAt={}, endAt={}",
                request.getFile(),
                request.getCategory(),
                request.getName(),
                request.getStatus(),
                request.getDescription(),
                request.getTicketNum(),
                request.getMinTicket(),
                request.getStartAt(),
                request.getEndAt());
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));


        // request 파일에서 url 추출
        String imageUrl = s3UploadService.saveFile(request.getFile());

        // 2. 요청받은 RaffleRequestDTO를 Raffle 엔티티로 변환
        Raffle raffle = RaffleConverter.toRaffle(request, category ,user, imageUrl);

        // 3. 변환 후 DB에 저장
        raffleRepository.save(raffle);

        raffleSchedulerService.scheduleRaffleJob(raffle, true);
        raffleSchedulerService.scheduleRaffleJob(raffle, false);

        // 4. Raffle 엔티티를 ResponseDTO로 변환하여 반환
        return RaffleConverter.toUploadResultDTO(raffle);
    }

    @Override
    public RaffleResponseDTO.RaffleDetailDTO getRaffleDetailsDTO(Long id) {

        // 1. 요청받은 래플id와 일치하는 Raffle 가져오기
        Raffle raffle = raffleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        // 2. 해당 raffle을 DetailDTO로 변환
        return RaffleConverter.toDetailDTO(raffle);
    }
}
