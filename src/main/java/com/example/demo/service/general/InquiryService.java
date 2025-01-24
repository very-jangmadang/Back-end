package com.example.demo.service.general;

import com.example.demo.domain.dto.Inquiry.InquiryAndCommentsResponseDTO;
import com.example.demo.domain.dto.Inquiry.InquiryDeleteDTO;
import com.example.demo.domain.dto.Inquiry.InquiryRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;

import java.util.List;

public interface InquiryService {
    // 문의글 작성
    InquiryResponseDTO addInquiry(InquiryRequestDTO inquiryRequest);

    // 문의글 삭제
    void deleteInquiry(Long InquiryId, InquiryDeleteDTO inquiryDelete);

    //문의글 조회
    List<InquiryAndCommentsResponseDTO> getInquiryAndComments(Long raffleId);
}

