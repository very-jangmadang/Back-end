package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Inquiry.*;
import com.example.demo.domain.dto.Review.ReviewResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.repository.InquiryRepository;
import com.example.demo.service.general.InquiryCommentService;
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
    private final InquiryRepository inquiryRepository;
    private final InquiryCommentService inquiryCommentService;

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

    // 문의 댓글 작성
    @PostMapping("/{inquiryId}/comment")
    public ApiResponse<InquiryCommentResponseDTO> addComment(
            @PathVariable Long inquiryId,
            @RequestBody InquiryCommentRequestDTO commentRequest) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.INQUIRY_NOT_FOUND));


        InquiryCommentResponseDTO commentResponse = inquiryCommentService.addComment(commentRequest,inquiryId);

        return ApiResponse.of(SuccessStatus._OK, commentResponse);

    }
}

