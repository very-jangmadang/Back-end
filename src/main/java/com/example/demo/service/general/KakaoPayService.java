package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.ApproveResponse;
import com.example.demo.domain.dto.Payment.CancelResponse;
import com.example.demo.domain.dto.Payment.PaymentRequest;
import com.example.demo.domain.dto.Payment.ReadyResponse;

public interface KakaoPayService {
    ApiResponse<ReadyResponse> preparePayment(PaymentRequest paymentRequest);
    ApiResponse<ApproveResponse> approvePayment(String pgToken, String tid);
    ApiResponse<CancelResponse> cancelPayment(Long userId);
}