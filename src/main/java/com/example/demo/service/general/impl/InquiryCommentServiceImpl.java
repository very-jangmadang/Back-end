package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.InquiryCommentConverter;
import com.example.demo.domain.converter.InquiryConverter;
import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.InquiryComment;
import com.example.demo.repository.InquiryCommentRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.InquiryCommentService;
import com.example.demo.service.general.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.repository.InquiryRepository;


@Service
@RequiredArgsConstructor
public class InquiryCommentServiceImpl implements InquiryCommentService {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryCommentRepository commentRepository;

    @Transactional
    public InquiryCommentResponseDTO addComment(InquiryCommentRequestDTO commentRequest,Long inquiryId) {

        // 사용자 조회
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.INQUIRY_NOT_FOUND));

        // 주최자 여부 확인
        boolean isHost = isRaffleHost(inquiry, user);

        // 댓글 작성
        InquiryComment comment = InquiryCommentConverter.toComment(commentRequest, user,isHost);
        commentRepository.save(comment);

        InquiryCommentResponseDTO commentResponse = InquiryCommentConverter.toCommentResponseDTO(comment);

        return commentResponse;
    }

    private boolean isRaffleHost(Inquiry inquiry, User user) {
        Raffle raffle = inquiry.getRaffle();
        return raffle.getUser().getId().equals(user.getId());
    }
}

