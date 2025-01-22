package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.service.general.RaffleService;
import com.example.demo.service.general.S3UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/permit")
public class RaffleController {

    private final RaffleService raffleService;
    private final S3UploadService s3UploadService;

    @PostMapping(value = "/raffles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RaffleResponseDTO.UploadResultDTO> upload(@ModelAttribute @Valid RaffleRequestDTO.UploadDTO request) {

        // 1. raffle 업로드 처리 : 서비스 계층에 요청
        RaffleResponseDTO.UploadResultDTO result = raffleService.uploadRaffle(request);

        // 2.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_UPLOAD_SUCCESS, result);
    }

    @GetMapping("/raffles/{raffleId}")
    public ApiResponse<RaffleResponseDTO.RaffleDetailDTO> getPostById(@PathVariable Long raffleId) {

        // 1. 래플id로 해당 detailDTO 받아오기
        RaffleResponseDTO.RaffleDetailDTO raffleDetailDTO = raffleService.getRaffleDetailsDTO(raffleId);

        // 2. 성공 응답 + 해당 detailDTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_FETCH_SUCCESS, raffleDetailDTO);
    }
}

