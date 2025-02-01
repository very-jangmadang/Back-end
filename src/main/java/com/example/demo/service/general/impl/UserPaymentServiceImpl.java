package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.UserPaymentConverter;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.Payment.Payment;
import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserPaymentRepository;
import com.example.demo.service.general.UserPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(UserPaymentServiceImpl.class);
    private final UserPaymentRepository userPaymentRepository;
    private final PaymentRepository paymentRepository;
    private final UserPaymentConverter userPaymentConverter;

    public UserPaymentServiceImpl (UserPaymentRepository userPaymentRepository, PaymentRepository paymentRepository, UserPaymentConverter userPaymentConverter) {
        this.userPaymentRepository = userPaymentRepository;
        this.paymentRepository = paymentRepository;
        this.userPaymentConverter = userPaymentConverter;
    }


    @Override
    public ApiResponse<UserTicketResponse> getUserTickets(String userId) {
        UserPayment userPayment = findOrCreateUserPayment(userId);

        UserTicketResponse response = new UserTicketResponse();
        response.setTicket(userPayment.getUserTicket());
        response.setUpdatedAt(userPayment.getUpdatedAt());

        return ApiResponse.of(SuccessStatus.USER_PAYMENT_GET_TICKET, response);
    }

    @Override
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(String userId, UserBankInfoRequest request) {
        UserPayment userPayment = findOrCreateUserPayment(userId);

        // 유저 결제 정보 업데이트 후 저장
        userPaymentConverter.updateUserPaymentWithBankInfo(userPayment, request);
        userPaymentRepository.save(userPayment);

        // 응답 객체 생성
        UserBankInfoResponse response = new UserBankInfoResponse();
        response.setBankName(userPayment.getBankName());
        response.setBankNumber(userPayment.getBankNumber());

        return ApiResponse.of(SuccessStatus.USER_PAYMENT_UPDATE_BANK_INFO, response);
    }

    private UserPayment findOrCreateUserPayment(String userId) {
        return userPaymentRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserPayment newUserPayment = userPaymentConverter.createDefaultUserPayment(userId);
                    return userPaymentRepository.save(newUserPayment);
                });
    }

    @Override
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(String userId, String period) {
        try {
            // 현재 시간 기준으로 조회할 기간 설정
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = switch (period) {
                case "7d" -> now.minusDays(7);
                case "1m" -> now.minusMonths(1);
                case "3m" -> now.minusMonths(3);
                case "6m" -> now.minusMonths(6);
                default -> throw new CustomException(ErrorStatus.USER_PAYMENT_INVALID_PERIOD); // 유효하지 않은 기간 입력 처리
            };

            Pageable pageable = PageRequest.of(0, 50); // 50개씩 페이징
            Page<Payment> payments = paymentRepository.findByUserIdAndApprovedAtAfterOrderByApprovedAtDesc(
                    userId, startDate, pageable);

            List<PaymentResponse> paymentHistory = payments.stream()
                    .map(userPaymentConverter::toPaymentResponse)
                    .collect(Collectors.toList());

            return ApiResponse.of(SuccessStatus.PAYMENT_HISTORY_SUCCESS, paymentHistory);
        } catch (Exception e) {
            logger.error("결제 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_HISTORY_ERROR);
        }
    }

}