package com.example.demo.service.general.impl;

import com.example.demo.base.code.exception.CustomException;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.domain.dto.VeryscanTx.VeryscanTxResponse;
import com.example.demo.entity.Payment.TopUp;
import com.example.demo.entity.User;
import com.example.demo.entity.base.enums.OrderStatus;
import com.example.demo.repository.TopUpRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketTopUpServiceImpl {

    private final TopUpRepository topUpRepository;
    private final UserRepository userRepository;

    @Value("${veryscan.base-url}")
    private String baseUrl;
    @Value("${veryscan.tx-path}")
    private String txPath;
    @Value("${veryscan.expected-to-address:}")
    private String expectedToAddress;
    @Value("${veryscan.rate-numerator}")
    private long rateNumerator;
    @Value("${veryscan.rate-denominator}")
    private long rateDenominator;

    /**
     * txid 검증 → 티켓 적립(중복 방지)
     */
    @Transactional
    public TopUpResult confirmTopUp(Long userId, String txid) {

        User user = userRepository.findById(userId).orElseThrow();

        // 0) 이미 처리했는지 먼저 확인 (idempotent)
        var existing = topUpRepository.findByTxId(txid);
        if (existing.isPresent()) {
            TopUp t = existing.get();
            return new TopUpResult(t.getStatus().name(), t.getTickets(), user.getTicket_num(), true,
                    t.getFromAddress(), t.getToAddress(), t.getTxId(), "이미 처리된 트랜잭션입니다.");
        }


        RestTemplate restTemplate = new RestTemplate();


        // 1) Veryscan 호출
        String url = baseUrl + txPath.replace("{txid}", txid);
        ResponseEntity<VeryscanTxResponse> res = restTemplate.getForEntity(url, VeryscanTxResponse.class);
        VeryscanTxResponse body = res.getBody();
        if (body == null) {
            throw new CustomException(ErrorStatus.PAYMENT_HISTORY_ERROR); // 적절한 에러로 교체
        }

//        // 2) 상태 검증
//        if (!"ok".equalsIgnoreCase(body.getStatus())) {
//            // 실패 영수증 저장
//            TopUp failed = saveTopUp(userId, txid, body, 0, OrderStatus.FAILED);
//            return new TopUpResult(failed.getStatus().name(), 0, user.getTicket_num(), false,
//                    failed.getFromAddress(), failed.getToAddress(), failed.getTxId(), "트랜잭션이 실패하였습니다.");
//        }

//        // 3) 수신 주소 검증(옵션)
//        if (expectedToAddress != null && !expectedToAddress.isBlank()) {
//            String toHash = body.getTo() != null ? body.getTo().getHash() : null;
//            if (toHash == null || !expectedToAddress.equalsIgnoreCase(toHash)) {
//                TopUp failed = saveTopUp(userId, txid, body, 0, OrderStatus.FAILED);
//                return new TopUpResult(failed.getStatus().name(), 0, getUserTickets(userId), false,
//                        failed.getFromAddress(), failed.getToAddress(), failed.getTxId());
//            }
//        }

        // 4) value → 티켓 변환 (문자열 BigDecimal 처리)
        //    tickets = floor( value * rateNumerator / rateDenominator )
        BigDecimal raw = new BigDecimal(body.getValue());
        BigDecimal numerator = BigDecimal.valueOf(rateNumerator);
        BigDecimal denominator = BigDecimal.valueOf(rateDenominator);
        int tickets = raw.multiply(numerator).divideToIntegralValue(denominator).intValue();

        if (tickets <= 0) {
            // 최소 적립 단위 미만 → 실패로 간주하거나 0 적립 처리
            TopUp failed = saveTopUp(userId, txid, body, 0, OrderStatus.FAILED);
            return new TopUpResult(failed.getStatus().name(), 0, user.getTicket_num(), false,
                    failed.getFromAddress(), failed.getToAddress(), failed.getTxId(), "구매한 티켓이 0이하입니다.");
        }

        // 5) 동시성/중복 방지: 유니크 제약 + 트랜잭션
        try {
            // 5-1) 사용자 티켓 증가
            user.setTicket_num(user.getTicket_num() + tickets);

            // 5-2) 영수증 저장
            TopUp saved = saveTopUp(userId, txid, body, tickets, OrderStatus.SUCCESS);

            return new TopUpResult(saved.getStatus().name(), tickets, user.getTicket_num(), false,
                    saved.getFromAddress(), saved.getToAddress(), saved.getTxId(), "트랜잭션 성공");
        } catch (DataIntegrityViolationException dup) {
            // 동시에 동일 txid 들어온 경우
            TopUp t = topUpRepository.findByTxId(txid).orElseThrow();
            return new TopUpResult(OrderStatus.DUPLICATE.name(), t.getTickets(), user.getTicket_num(), true,
                    t.getFromAddress(), t.getToAddress(), t.getTxId(), "중복된 트랜잭션입니다.");
        }
    }

    private TopUp saveTopUp(Long userId, String txid, VeryscanTxResponse body, int tickets, OrderStatus status) {
        TopUp t = new TopUp();
        t.setUserId(userId);
        t.setTxId(txid);
        t.setRawValue(body.getValue());
        t.setTickets(tickets);
        t.setStatus(status);
        t.setFromAddress(body.getFrom() != null ? body.getFrom().getHash() : null);
        t.setToAddress(body.getTo() != null ? body.getTo().getHash() : null);
        // timestamp 예: "2025-08-20T10:55:31.000000Z" → 파싱 실패시 now
        try {
            Instant instant = Instant.parse(body.getTimestamp());
            t.setConfirmedAt(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        } catch (Exception ignore) {
            t.setConfirmedAt(LocalDateTime.now());
        }
        return topUpRepository.save(t);
    }


    @lombok.Data
    @lombok.AllArgsConstructor
    public static class TopUpResult {
        private String status;   // SUCCESS / FAILED / DUPLICATE
        private int increased;   // 이번 적립 티켓 수
        private int balance;     // 현재 잔액
        private boolean idempotent;
        private String from;
        private String to;
        private String txid;
        private String message;
    }
}
