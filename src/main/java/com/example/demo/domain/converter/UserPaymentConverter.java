package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.Payment.Payment;
import com.example.demo.entity.Payment.UserPayment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserPaymentConverter {

    public UserPayment createDefaultUserPayment(String userId) {
        UserPayment userPayment = new UserPayment();
        userPayment.setUserId(userId);
        userPayment.setUserTicket(0); // 기본 티켓 수 0으로 설정
        userPayment.setBankName("Default J-MARKET Bank"); // 기본 은행 이름 설정
        userPayment.setBankNumber("000-000-0000"); // 기본 계좌번호 설정
        userPayment.setUpdatedAt(LocalDateTime.now());
        return userPayment;
    }

    public void updateUserPaymentWithBankInfo(UserPayment userPayment, UserBankInfoRequest request) {
        userPayment.setBankName(request.getBankName());
        userPayment.setBankNumber(request.getBankNumber());
        userPayment.setUpdatedAt(LocalDateTime.now());
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getApprovedAt(),
                payment.getQuantity(),
                "카카오페이",
                payment.getAmount()
        );
    }

}