package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.ExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.ExchangeRequest;
import com.example.demo.domain.dto.Payment.ExchangeResponse;

import java.util.List;

public interface ExchangeService {
    // 환전 처리
    ApiResponse<ExchangeResponse> exchange(String userId, ExchangeRequest request);
    // 환전 내역 조회 (기간별 조회)
    ApiResponse<List<ExchangeHistoryResponse>> getExchangeHistory(String userId, String period);
}