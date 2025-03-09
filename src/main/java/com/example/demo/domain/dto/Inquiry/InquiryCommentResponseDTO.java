package com.example.demo.domain.dto.Inquiry;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InquiryCommentResponseDTO {
    private Long CommentId;
    private Long userId;
    private Long inquiryId;
    private Long raffleId;
    private String nickname;
    private String title;
    private String content;
    private boolean isHost;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
