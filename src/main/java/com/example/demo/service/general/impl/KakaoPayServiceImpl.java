package com.example.demo.service.general.impl;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.converter.KakaoPayConverter;
import com.example.demo.domain.converter.UserPaymentConverter;
import com.example.demo.domain.dto.Payment.ApproveResponse;
import com.example.demo.domain.dto.Payment.CancelResponse;
import com.example.demo.domain.dto.Payment.PaymentRequest;
import com.example.demo.domain.dto.Payment.ReadyResponse;
import com.example.demo.entity.Payment.Payment;
import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.entity.User;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserPaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.general.KakaoPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class KakaoPayServiceImpl implements KakaoPayService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoPayServiceImpl.class);
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final UserPaymentRepository userPaymentRepository;
    private final UserPaymentConverter userPaymentConverter;
    private final KakaoPayConverter kakaoPayConverter;
    private final String secretKey;
    private final UserRepository userRepository;

    public KakaoPayServiceImpl(
            @Value("${kakao.pay.cid}") String cid,
            @Value("${kakao.pay.secretKey}") String secretKey,
            @Value("${kakao.pay.approvalUrl}") String approvalUrl,
            @Value("${kakao.pay.cancelUrl}") String cancelUrl,
            @Value("${kakao.pay.failUrl}") String failUrl,
            PaymentRepository paymentRepository,
            UserPaymentRepository userPaymentRepository,
            UserPaymentConverter userPaymentConverter,
            UserRepository userRepository
    ) {
        this.secretKey = secretKey;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.userPaymentRepository = userPaymentRepository;
        this.restTemplate = new RestTemplate();
        this.userPaymentConverter = userPaymentConverter;
        this.kakaoPayConverter = new KakaoPayConverter(cid, approvalUrl, cancelUrl, failUrl, userRepository);
    }


    // 결제 준비 요청
    @Override
    public ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest) {

        Map<String, Object> parameters = kakaoPayConverter.toPrepareParameters(paymentRequest);
        ReadyResponse readyResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                parameters,
                ReadyResponse.class
        );

        // API 응답 데이터를 기반으로 Payment 엔티티 저장
        Optional.ofNullable(readyResponse).ifPresent(response -> {
            Payment paymentEntity = kakaoPayConverter.toEntity(findUser(paymentRequest.getUserId()), paymentRequest, response);
            savePaymentEntity(paymentEntity);
        });
        return ApiResponse.of(SuccessStatus.PAYMENT_READY_SUCCESS, readyResponse);
    }

    // 결제 승인 요청
    @Override
    public ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid) {

        Payment payment = findPaymentByTid(tid);
        ApproveResponse approveResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                kakaoPayConverter.toApproveParameters(payment, pgToken),
                ApproveResponse.class
        );

        Optional.ofNullable(approveResponse).ifPresent(response -> {
            payment.setStatus("APPROVED");
            payment.setApprovedAt(LocalDateTime.now());
            payment.setAmount(payment.getAmount()/100);
            savePaymentEntity(payment);

            UserPayment userPayment = findOrCreateUser(payment.getUser().getId());
            User user = payment.getUser();

            // 유저 티켓 수 업데이트 (배송비가 아닌 경우)
            if (!payment.getItemId().equals("배송비")) {
                int updatedTickets = user.getTicket_num() + payment.getAmount();
                user.setTicket_num(updatedTickets);
                userPayment.setUpdatedAt(LocalDateTime.now());
            }

            // 변경된 유저 정보 저장
            userPaymentRepository.save(userPayment);
        });

        return ApiResponse.of(SuccessStatus.PAYMENT_APPROVE_SUCCESS, approveResponse);
    }

    private UserPayment findOrCreateUser(Long userId) {
        return userPaymentRepository.findByUserId(userId)
                .orElseGet(() -> userPaymentRepository.save(userPaymentConverter.createDefaultUserPayment(findUser(userId))));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
    }

    private Payment findPaymentByTid(String tid) {
        return paymentRepository.findByTid(tid)
                .orElseThrow(() -> new CustomException(ErrorStatus.PAYMENT_NOT_FOUND));
    }

    private void savePaymentEntity(Payment paymentEntity) {
        try {
            paymentRepository.save(paymentEntity);
        } catch (Exception e) {
            logger.error("결제 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_SAVE_FAILED);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private <T> T sendRequest(String url, Map<String, Object> parameters, Class<T> responseType) {
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, createHeaders());
            ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                logger.error("API 요청 실패: 상태 코드 - {}", response.getStatusCode());
                throw new CustomException(ErrorStatus.PAYMENT_REQUEST_FAILED);
            }
        } catch (Exception e) {
            logger.error("API 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.PAYMENT_REQUEST_ERROR);
        }
    }

    // 결제 취소
    @Override
    public ApiResponse<CancelResponse> cancelPayment(Long userId) {
        Payment payment = paymentRepository.findTopByUserIdAndStatusAndItemIdOrderByApprovedAtDesc(userId, "APPROVED", "배송비");
        if (payment == null) {
            throw new CustomException(ErrorStatus.PAYMENT_NOT_FOUND);
        }

        CancelResponse cancelResponse = sendRequest(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                kakaoPayConverter.toCancelParameters(payment),
                CancelResponse.class
        );
        payment.setStatus("CANCELLED");
        savePaymentEntity(payment);

        return ApiResponse.of(SuccessStatus.PAYMENT_CANCEL_SUCCESS, cancelResponse);

    }
}
