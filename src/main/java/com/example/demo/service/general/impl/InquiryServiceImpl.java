package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.domain.converter.InquiryConverter;
import com.example.demo.domain.converter.LikeConverter;
import com.example.demo.domain.dto.Inquiry.InquiryDeleteDTO;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.Inquiry.InquiryRequestDTO;
import com.example.demo.domain.dto.Inquiry.InquiryResponseDTO;
import com.example.demo.domain.dto.Like.LikeResponseDTO;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.Raffle;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.InquiryStatus;
import com.example.demo.repository.RaffleRepository;
import com.example.demo.service.general.InquiryService;
import lombok.RequiredArgsConstructor;
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

    // 문의 작성
    public InquiryResponseDTO addInquiry(InquiryRequestDTO inquiryRequest) {

        User user = userRepository.findById(inquiryRequest.getUserId())
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
    public void deleteInquiry(Long inquiryId, InquiryDeleteDTO inquiryDelete) {

        // 삭제
        inquiryRepository.deleteById(inquiryId);

    }

    //문의 조회
    public List<InquiryResponseDTO> getInquiriesByRaffleId(Long raffleId) {

        // 래플 조회
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new CustomException(ErrorStatus.RAFFLE_NOT_FOUND));

        // 래플의 모든 문의 조회
        List<Inquiry> inquiries = inquiryRepository.findAllByRaffle(raffle);

        return inquiries.stream()
                .map(inquiry -> new InquiryResponseDTO(
                        inquiry.getId(),              // inquiryId
                        inquiry.getUser().getId(),    // userId
                        inquiry.getRaffle().getId(),  // raffleId
                        inquiry.getTitle(),           // title
                        inquiry.getContent(),          //content
                        inquiry.getStatus(),          // status
                        inquiry.getCreatedAt()        // timestamp
                ))
                .collect(Collectors.toList());
    }
}

