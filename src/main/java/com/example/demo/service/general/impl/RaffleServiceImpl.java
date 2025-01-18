package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Image;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.ImageService;
import com.example.demo.service.general.RaffleService;
import com.example.demo.service.general.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RaffleServiceImpl implements RaffleService {

    private final RaffleRepository raffleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final ImageService imageService;

    @Override
    @Transactional
    public RaffleResponseDTO.UploadResultDTO uploadRaffle(RaffleRequestDTO.UploadDTO request) {

        // 0. 업로드 작성자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 1. 요청받은 카테고리 이름으로 Category 엔티티 가져오기
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 2. S3에 파일 업로드
        List<String> imageUrls = s3UploadService.saveFile(request.getFiles());

        // 3. 래플 생성
        Raffle raffle = RaffleConverter.toRaffle(request, category ,user);

        // 4. 이미지 엔티티 생성
        List<Image> images = imageService.saveImages(imageUrls);

        // 5. 래플과 이미지 엔티티 매핑
        images.forEach(img -> raffle.addImage(img));

        // 6. 래플 저장
        raffleRepository.save(raffle);

        // 7. 래플 엔티티를 ResponseDTO로 변환 후 반환
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
