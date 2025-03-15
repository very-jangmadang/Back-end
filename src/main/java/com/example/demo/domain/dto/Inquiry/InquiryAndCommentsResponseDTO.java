package com.example.demo.domain.dto.Inquiry;

import com.example.demo.entity.base.enums.InquiryStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private List<InquiryCommentResponseDTO> comments;
}
