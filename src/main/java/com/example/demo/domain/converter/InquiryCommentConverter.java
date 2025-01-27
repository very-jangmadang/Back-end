package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.entity.InquiryComment;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class InquiryCommentConverter {

    public static InquiryComment toComment(InquiryCommentRequestDTO CommentRequest, User user, boolean isHost) {
        return InquiryComment.builder()
                .user(user)
                .content(CommentRequest.getContent())
                .isHost(isHost)
                .build();
    }

    public static InquiryCommentResponseDTO toCommentResponseDTO(InquiryComment comment) {

        return InquiryCommentResponseDTO.builder()
                .CommentId(comment.getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .isHost(comment.isHost())
                .timestamp(LocalDateTime.now())
                .build();
    }

}

