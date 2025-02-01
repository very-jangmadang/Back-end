package com.example.demo.service.general;

import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.domain.dto.Inquiry.InquiryRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import org.springframework.security.core.Authentication;

public interface InquiryCommentService {

    //문의 댓글 작성
    InquiryCommentResponseDTO addComment(InquiryCommentRequestDTO commentRequest, Long inquiryId, Long userId);

}
