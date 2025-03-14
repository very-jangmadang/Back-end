package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Raffle.RaffleRequestDTO;
import com.example.demo.domain.dto.Raffle.RaffleResponseDTO;
import com.example.demo.service.general.RaffleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.base.status.SuccessStatus._OK;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class RaffleController {

    private final RaffleService raffleService;
    @Operation(summary = "래플 업로드")
    @PostMapping(value = "/api/member/raffles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RaffleResponseDTO.UploadResultDTO> upload(@ModelAttribute @Valid RaffleRequestDTO.UploadDTO request) {

        // 1. raffle 업로드 처리 : 서비스 계층에 요청
        RaffleResponseDTO.UploadResultDTO result = raffleService.uploadRaffle(request);

        // 2.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_UPLOAD_SUCCESS, result);
    }

    @Operation(summary = "래플 상세보기")
    @GetMapping("/api/permit/raffles/{raffleId}")
    public ApiResponse<RaffleResponseDTO.RaffleDetailDTO> getPostById(@PathVariable Long raffleId) {

        // 1. 래플id로 해당 detailDTO 받아오기
        RaffleResponseDTO.RaffleDetailDTO result = raffleService.getRaffleDetailsDTO(raffleId);

        // 2. 성공 응답 + 해당 detailDTO 반환
        return ApiResponse.of(SuccessStatus.RAFFLE_FETCH_SUCCESS, result);
    }

    @Operation(summary = "래플 삭제하기")
    @DeleteMapping("/api/member/raffles/{raffleId}")
    public ApiResponse<?> deletePostById(@PathVariable Long raffleId) {
        raffleService.deleteRaffle(raffleId);
        return ApiResponse.of(SuccessStatus._OK, raffleId);
    }

    @Operation(summary = "래플 응모하기")
    @PostMapping("/api/member/raffles/{raffleId}/apply")
    public ApiResponse<?> apply(@PathVariable Long raffleId) {

        RaffleResponseDTO.ApplyResultDTO resultDTO = raffleService.apply(raffleId);

        if (resultDTO.getApplyDTO() == null)
            return ApiResponse.onFailure(ErrorStatus.APPLY_INSUFFICIENT_TICKET, resultDTO.getFailedApplyDTO());

        return ApiResponse.of(_OK, resultDTO.getApplyDTO());
    }
}

