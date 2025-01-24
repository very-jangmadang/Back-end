package com.example.demo.domain.dto.Inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAndCommentsResponseDTO {
    private Long inquiryId;
    private String inquiryContent;
    private List<InquiryCommentResponseDTO> comments;
}
