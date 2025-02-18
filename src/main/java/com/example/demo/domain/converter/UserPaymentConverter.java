package com.example.demo.domain.converter;

import com.example.demo.domain.dto.Payment.*;
import com.example.demo.entity.Payment.Payment;
import com.example.demo.entity.Payment.UserPayment;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserPaymentConverter {

    public UserPayment createDefaultUserPayment(String userId) {
        UserPayment userPayment = new UserPayment();
        userPayment.setUserId(userId);
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

    public PaymentResponse toPaymentResponse(Payment payment, User user) {
        return new PaymentResponse(
                payment.getId(),
                payment.getApprovedAt(),
                user.getTicket_num(),
                "카카오페이",
                payment.getAmount()
        );
    }

}