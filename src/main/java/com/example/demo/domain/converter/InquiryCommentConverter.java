package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryComment;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class InquiryCommentConverter {

    public static InquiryComment toComment(InquiryCommentRequestDTO CommentRequest, User user, boolean isHost, Inquiry inquiry) {
        return InquiryComment.builder()
                .user(user)
                .title(CommentRequest.getTitle())
                .content(CommentRequest.getContent())
                .inquiry(inquiry)
                .isHost(isHost)
                .build();
    }

    public static InquiryCommentResponseDTO toCommentResponseDTO(InquiryComment comment) {

        return InquiryCommentResponseDTO.builder()
                .CommentId(comment.getId())
                .userId(comment.getUser().getId())
                .inquiryId(comment.getInquiry().getId())
                .raffleId(comment.getInquiry().getRaffle().getId())
                .nickname(comment.getUser().getNickname())
                .title(comment.getTitle())
                .content(comment.getContent())
                .isHost(comment.isHost())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}

