package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.ExchangeConverter;
import com.example.demo.domain.dto.Payment.ExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.ExchangeRequest;
import com.example.demo.domain.dto.Payment.ExchangeResponse;
import com.example.demo.entity.Payment.Exchange;
import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.entity.User;
import com.example.demo.repository.ExchangeRepository;
import com.example.demo.repository.UserPaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.ExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private static final Logger logger = LoggerFactory.getLogger(UserPaymentServiceImpl.class);
    private final ExchangeRepository exchangeRepository;
    private final ExchangeConverter exchangeConverter;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<ExchangeResponse> exchange(Long userId, ExchangeRequest request) {

        // 유저 결제 정보 조회 (없으면 예외 발생)
        User user = findUser(userId);

        // 환전 가능 여부 체크 (잔액 부족 시 오류)
        if (user.getTicket_num() < request.getAmount() ) {
            throw new CustomException(ErrorStatus.USER_INSUFFICIENT_BALANCE);
        }

        // 환전 처리 (아직 실제 로직 구현 안함. 티켓만 차감됩니다.)
        user.setTicket_num(user.getTicket_num() - request.getAmount());
        userRepository.save(user);

        Exchange exchange = exchangeConverter.toEntity(user, request);
        exchangeRepository.save(exchange);

        // API 응답 반환
        ExchangeResponse exchangeResponse = new ExchangeResponse("환전 성공", exchange.getExchangedAt());
       
        return ApiResponse.of(SuccessStatus.PAYMENT_APPROVE_SUCCESS, exchangeResponse);
    }

    @Override
    public ApiResponse<List<ExchangeHistoryResponse>> getExchangeHistory(Long userId, String period) {
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
            Page<Exchange> exchanges = exchangeRepository.findByUserIdAndExchangedAtAfterOrderByExchangedAtDesc(
                    userId, startDate, pageable);

            User user = findUser(userId);
            List<ExchangeHistoryResponse> exchangeHistory = exchanges.stream()
                    .map(exchange -> exchangeConverter.toExchangeHistoryResponse(
                            exchange, user))
                    .collect(Collectors.toList());

            return ApiResponse.of(SuccessStatus.PAYMENT_HISTORY_SUCCESS, exchangeHistory);
        } catch (Exception e) {
            logger.error("교환 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_HISTORY_ERROR);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

}