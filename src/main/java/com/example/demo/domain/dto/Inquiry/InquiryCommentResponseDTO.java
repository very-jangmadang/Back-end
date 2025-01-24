package com.example.demo.domain.dto.Inquiry;

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
public class InquiryCommentResponseDTO {
    private Long CommentId;
    private Long userId;
    private Long raffleId;
    private String nickname;
    private String content;
    private boolean isHost;
    private LocalDateTime timestamp;

}
