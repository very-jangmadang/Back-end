package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.repository.InquiryRepository;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.service.general.InquiryCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/permit/inquiry")
@RequiredArgsConstructor
public class InquiryCommentController {

    private final InquiryCommentService inquiryCommentService;
    private final InquiryRepository inquiryRepository;

    // 문의 댓글 작성
    @PostMapping("/{inquiryId}/comment")
    public ApiResponse<InquiryCommentResponseDTO> addComment(
            @PathVariable Long inquiryId,
            @RequestBody  InquiryCommentRequestDTO commentRequest) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.INQUIRY_NOT_FOUND));


        InquiryCommentResponseDTO commentResponse = inquiryCommentService.addComment(commentRequest);

        return com.example.demo.base.ApiResponse.of(SuccessStatus._OK, commentResponse);

    }

}