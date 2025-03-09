package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Inquiry.InquiryRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class InquiryConverter {

    public static Inquiry toInquiry(InquiryRequestDTO Inquiryrequest, User user, Raffle raffle) {
        return Inquiry.builder()
                .user(user)
                .raffle(raffle)
                .title(Inquiryrequest.getTitle())
                .content(Inquiryrequest.getContent())
                .build();
    }

    public static InquiryResponseDTO ToInquiryResponseDTO(Inquiry inquiry) {

        return InquiryResponseDTO.builder()
                .inquiryId(inquiry.getId())
                .userId(inquiry.getUser().getId())
                .raffleId(inquiry.getRaffle().getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .nickname(inquiry.getUser().getNickname())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }

}

