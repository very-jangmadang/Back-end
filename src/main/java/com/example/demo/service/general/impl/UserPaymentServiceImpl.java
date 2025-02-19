package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.UserPaymentConverter;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.Payment.Payment;
import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.entity.User;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserPaymentRepository;
import com.example.demo.repository.UserRepository;
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
    private final UserRepository userRepository;

    public UserPaymentServiceImpl (UserRepository userRepository, UserPaymentRepository userPaymentRepository, PaymentRepository paymentRepository, UserPaymentConverter userPaymentConverter) {
        this.userPaymentRepository = userPaymentRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.userPaymentConverter = userPaymentConverter;
    }


    @Override
    public ApiResponse<UserTicketResponse> getUserTickets(String userId) {
        User user = findUser(userId);

        UserTicketResponse response = new UserTicketResponse();
        response.setTicket(user.getTicket_num());
        response.setUpdatedAt(user.getUpdatedAt());

        return ApiResponse.of(SuccessStatus.USER_PAYMENT_GET_TICKET, response);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public ApiResponse<UserBankInfoResponse> postUserPaymentInfo(String userId, UserBankInfoRequest request) {
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

    @Override
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(String userId) {
        UserPayment userPayment = findOrCreateUserPayment(userId);

        // 응답 객체 생성
        UserBankInfoResponse response = new UserBankInfoResponse();
        response.setBankName(userPayment.getBankName());
        response.setBankNumber(userPayment.getBankNumber());

        return ApiResponse.of(SuccessStatus.USER_PAYMENT_GET_BANK_INFO, response);
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
                case "recent" -> now.minusYears(100); // 충분히 조회되도록 설정
                case "7d" -> now.minusDays(7);
                case "1m" -> now.minusMonths(1);
                case "3m" -> now.minusMonths(3);
                case "6m" -> now.minusMonths(6);
                default -> throw new CustomException(ErrorStatus.USER_PAYMENT_INVALID_PERIOD);
            };

            Pageable pageable = period.equals("recent") ? PageRequest.of(0, 1) : PageRequest.of(0, 50);
            Page<Payment> payments = paymentRepository.findByUserIdAndApprovedAtAfterOrderByApprovedAtDesc(
                    userId, startDate, pageable);

            User user = findUser(userId);
            List<PaymentResponse> paymentHistory = payments.stream()
                    .map(payment -> userPaymentConverter.toPaymentResponse(
                            payment, user))
                    .collect(Collectors.toList());

            return ApiResponse.of(SuccessStatus.PAYMENT_HISTORY_SUCCESS, paymentHistory);
        } catch (Exception e) {
            logger.error("결제 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_HISTORY_ERROR);
        }
    }

    @Override
    public ApiResponse<Void> tradeTickets(String userId, String role, int ticketCount) {
        if (ticketCount < 1) {
            throw new CustomException(ErrorStatus.TRADE_INVALID_TICKET_COUNT);
        }

        UserPayment userPayment = findOrCreateUserPayment(userId);
        User user = findUser(userId);

        switch (role) {
            case "구매자":
                if (user.getTicket_num() < ticketCount) {
                    throw new CustomException(ErrorStatus.TRADE_INSUFFICIENT_TICKETS);
                }
                user.setTicket_num(user.getTicket_num() - ticketCount);
                break;

            case "판매자":
                user.setTicket_num(user.getTicket_num() + ticketCount);
                break;

            default:
                throw new CustomException(ErrorStatus.TRADE_INVALID_ROLE);
        }

        userPayment.setUpdatedAt(LocalDateTime.now());
        userPaymentRepository.save(userPayment);

        return ApiResponse.of(SuccessStatus.TRADE_TICKET_SUCCESS, null);
    }

}