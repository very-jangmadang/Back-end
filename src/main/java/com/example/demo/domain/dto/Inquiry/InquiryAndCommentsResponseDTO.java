package com.example.demo.domain.dto.Inquiry;

import com.example.demo.entity.base.enums.InquiryStatus;
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
    private String inquiryTitle;
    private String nickname;
    private String inquiryContent;
    private InquiryStatus status;
    private List<InquiryCommentResponseDTO> comments;
}
