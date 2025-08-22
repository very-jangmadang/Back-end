package com.example.demo.service.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.domain.dto.TopUp.TopUpResponse;

import java.util.List;

public interface UserPaymentService {
    ApiResponse<UserTicketResponse> getUserTickets(Long userId);
    ApiResponse<UserBankInfoResponse> postUserPaymentInfo(Long userId, UserBankInfoRequest userBankInfoRequest);
    ApiResponse<UserBankInfoResponse> getUserPaymentInfo(Long userId);
    ApiResponse<List<TopUpResponse>> getTopUpHistory(Long userId, String period);
    ApiResponse<Void> tradeTickets(Long userId, String role, int ticketCount);
}