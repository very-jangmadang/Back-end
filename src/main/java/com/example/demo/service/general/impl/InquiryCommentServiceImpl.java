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
import com.example.demo.entity.base.enums.InquiryStatus;
import com.example.demo.repository.InquiryCommentRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.InquiryCommentService;
import com.example.demo.service.general.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    public InquiryCommentResponseDTO addComment(InquiryCommentRequestDTO commentRequest, Long inquiryId, Long userId) {

        // 로그인된 사용자 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 문의 조회
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.INQUIRY_NOT_FOUND));

        // 주최자 여부 확인
        boolean isHost = isRaffleHost(inquiry, user);

        // 문의 상태 업데이트
        if (isHost) {
            inquiry.setStatus(InquiryStatus.ANSWERED); // 주최자가 댓글 작성
        } else {
            if ( inquiry.getStatus() == null||inquiry.getStatus() != InquiryStatus.ANSWERED) {
                inquiry.setStatus(InquiryStatus.NOT_ANSWERED); // 주최자가 댓글 미작성
            }
        }

        // 댓글 작성
        InquiryComment comment = InquiryCommentConverter.toComment(commentRequest, user,isHost,inquiry);
        commentRepository.save(comment);

        InquiryCommentResponseDTO commentResponse = InquiryCommentConverter.toCommentResponseDTO(comment);

        // inquiryId도 포함하여 반환
        commentResponse = commentResponse.toBuilder()
                .inquiryId(inquiryId)  // inquiryId 추가
                .build();


        return commentResponse;
    }

    private boolean isRaffleHost(Inquiry inquiry, User user) {
        Raffle raffle = inquiry.getRaffle();
        return raffle.getUser().getId().equals(user.getId());
    }
}

