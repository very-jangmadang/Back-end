package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.converter.InquiryCommentConverter;
import com.example.demo.domain.converter.InquiryConverter;
import com.example.demo.domain.dto.Inquiry.InquiryCommentRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryCommentResponseDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.User;
import com.example.demo.entity.InquiryComment;
import com.example.demo.repository.InquiryCommentRepository;
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

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final InquiryCommentRepository commentRepository;

    @Transactional
    public InquiryCommentResponseDTO addComment(InquiryCommentRequestDTO commentRequest) {
        // 사용자 조회
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 댓글 작성
        InquiryComment comment = InquiryCommentConverter.toComment(commentRequest, user);
        commentRepository.save(comment);

        InquiryCommentResponseDTO commentResponse = InquiryCommentConverter.toCommentResponseDTO(comment);

        return commentResponse;
    }
}

