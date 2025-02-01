package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.domain.converter.InquiryCommentConverter;
import com.example.demo.domain.converter.InquiryConverter;
import com.example.demo.domain.converter.LikeConverter;
import com.example.demo.domain.dto.Inquiry.*;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryComment;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.InquiryStatus;
import com.example.demo.repository.InquiryCommentRepository;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.demo.repository.InquiryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final RaffleRepository raffleRepository;
    private final InquiryCommentRepository inquiryCommentRepository;

    // 문의 작성
    public InquiryResponseDTO addInquiry(InquiryRequestDTO inquiryRequest,Long userId) {

        //로그인된 사용자 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Raffle raffle = raffleRepository.findById(inquiryRequest.getRaffleId())
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        // inquiry 객체 생성
        Inquiry inquiry = InquiryConverter.toInquiry(inquiryRequest, user, raffle);

        //NOT_ANSWERED로 초기화
        if (inquiry.getStatus() == null) {
            inquiry.setStatus(InquiryStatus.NOT_ANSWERED);
        }

        inquiryRepository.save(inquiry);

        InquiryResponseDTO inquiryResponse = InquiryConverter.ToInquiryResponseDTO(inquiry);

        return inquiryResponse;
    }

    // 문의 삭제
    public void deleteInquiry(Long inquiryId, Long userId) {

        // 문의 내역 조회
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.INQUIRY_NOT_FOUND));

        // 문의 작성자와 삭제 요청자의 사용자 ID 비교
        if (!inquiry.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.NO_DELETE_AUTHORITY); // 삭제 권한이 없는 경우
        }
        // 삭제
        inquiryRepository.deleteById(inquiryId);

    }

    //문의와 댓글조회
    public List<InquiryAndCommentsResponseDTO> getInquiryAndComments(Long raffleId) {

        // Raffle 조회
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        // 해당 래플의 모든 Inquiry 조회
        List<Inquiry> inquiries = inquiryRepository.findByRaffle(raffle);

        // 각 문의에 대한 댓글들
        return inquiries.stream()
                .map(inquiry -> {

                    List<InquiryComment> comments = inquiryCommentRepository.findAllByInquiryId(inquiry.getId());
                    List<InquiryCommentResponseDTO> commentDTOs = comments.stream()
                            .map(InquiryCommentConverter::toCommentResponseDTO)
                            .collect(Collectors.toList());

                    return new InquiryAndCommentsResponseDTO(inquiry.getId(),inquiry.getTitle(),inquiry.getUser().getNickname(),
                            inquiry.getContent(),inquiry.getStatus(), inquiry.getCreatedAt(),commentDTOs);
                })
                .collect(Collectors.toList());
    }

}

