package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Inquiry.InquiryDeleteDTO;
import com.example.demo.domain.dto.Inquiry.InquiryRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.service.general.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/permit/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    //문의글 작성
    @Operation(summary = "문의글 작성")
    @PostMapping("/")
    public ApiResponse<InquiryResponseDTO> addInquiry(
            @RequestBody InquiryRequestDTO inquiryRequest) {

        InquiryResponseDTO inquiryResponse = inquiryService.addInquiry(inquiryRequest);

        return ApiResponse.of(SuccessStatus._OK, inquiryResponse);

    }


    //문의글 삭제
    @Operation(summary = "문의글 삭제")
    @DeleteMapping("/{inquiryId}")
    public ApiResponse<InquiryResponseDTO> deleteInquiry(
            @PathVariable Long inquiryId,
            @RequestBody InquiryDeleteDTO inquiryDelete) {

        inquiryService.deleteInquiry(inquiryId,inquiryDelete);

        return ApiResponse.of(SuccessStatus._OK, null);
    }

    //문의 목록 조회
    @GetMapping("/{raffleId}/inquiry")
    public ApiResponse<List<InquiryResponseDTO>> getInquiriesByRaffleId(@PathVariable Long raffleId) {

        List<InquiryResponseDTO> inquiries = inquiryService.getInquiriesByRaffleId(raffleId);

        return ApiResponse.of(SuccessStatus._OK, inquiries);
    }
}

