package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.RaffleConverter;
import com.example.demo.domain.dto.RaffleRequestDTO;
import com.example.demo.domain.dto.RaffleResponseDTO;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.RaffleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/permit")
public class RaffleController {

    private final RaffleService raffleService;
    private final UserRepository userRepository;

    @PostMapping("/raffles")
    public ApiResponse<RaffleResponseDTO.UploadResultDTO> upload(@RequestBody @Valid RaffleRequestDTO.UploadDTO request) {

        // 1. 업로드 작성자 정보 가져오기 (JWT 기반 인증 후 추후 구현 예정)
        User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 2. raffle 업로드 처리 : 서비스 계층에 요청
        Raffle raffle = raffleService.uploadRaffle(request, user);

        // 3.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_UPLOAD_SUCCESS, RaffleConverter.toUploadResultDTO(raffle));
    }

    @GetMapping("/raffles/{raffleId}")
    public ApiResponse<RaffleResponseDTO.RaffleDetailDTO> getPostById(@PathVariable Long raffleId) {

        // 1. 래플id로 해당 detailDTO 받아오기

        RaffleResponseDTO.RaffleDetailDTO raffleDetailDTO = raffleService.getRaffleDetailsDTO(raffleId);

        // 2. 성공 응답 + 해당 detailDTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_FETCH_SUCCESS, raffleDetailDTO);
    }
}
