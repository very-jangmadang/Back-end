package com.example.demo.domain.dto.Inquiry;

import lombok.Getter;

@Getter
public class InquiryCommentRequestDTO {
    private Long userId;
    private String content;
    private Long raffleId;
}
