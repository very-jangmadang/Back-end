package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Report.RaffleReportRequestDTO;
import com.example.demo.domain.dto.Report.RaffleReportResponseDTO;
import com.example.demo.domain.dto.Report.UserReportRequestDTO;
import com.example.demo.domain.dto.Report.UserReportResponseDTO;
import com.example.demo.service.general.UserReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class ReportController {

    private final UserReportService userReportService;

    @Operation(summary = "유저 신고")
    @PostMapping(value = "/users/{userId}/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserReportResponseDTO> reportUser(
            @PathVariable Long userId,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") UserReportRequestDTO request,
            @RequestPart(value = "reviewPicture", required = false) List<MultipartFile> images
    ) {

        UserReportResponseDTO response = userReportService.reportUser(userId, request, images);

        return ApiResponse.of(SuccessStatus._OK, response);
    }

    @Operation(summary = "게시글 신고")
    @PostMapping(value = "/raffles/{raffleId}/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RaffleReportResponseDTO> reportRaffle(
            @PathVariable Long raffleId,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") RaffleReportRequestDTO request,
            @RequestPart (value = "reviewPicture", required = false) List<MultipartFile> images
    ) {
        RaffleReportResponseDTO response = userReportService.reportRaffle(raffleId, request, images);

        return ApiResponse.of(SuccessStatus._OK, response);
    }
}
