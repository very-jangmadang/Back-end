package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.entity.InquiryComment;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class InquiryCommentConverter {

    public static InquiryComment toComment(InquiryCommentRequestDTO CommentRequest, User user) {
        return InquiryComment.builder()
                .seller(user)
                .content(CommentRequest.getContent())
                .build();
    }

    public static InquiryCommentResponseDTO toCommentResponseDTO(InquiryComment comment) {

        return InquiryCommentResponseDTO.builder()
                .CommentId(comment.getId())
                .userId(comment.getSeller().getId())
                .content(comment.getContent())
                .timestamp(LocalDateTime.now())
                .build();
    }

}

