package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.domain.dto.TopUp.TopUpResponse;
import com.example.demo.service.general.UserPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/payment")
public class PaymentController {

    private final UserPaymentService userPaymentService;
    private final BaseController baseController;

    @GetMapping("/tickets")
    public ApiResponse<UserTicketResponse> getUserTickets() {
        Long userId = baseController.getCurrentUserId();
        return userPaymentService.getUserTickets(userId);
    }

    @GetMapping("/bankInfo")
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo() {
        Long userId = baseController.getCurrentUserId();
        return userPaymentService.getUserPaymentInfo(userId);
    }

    @PostMapping("/bankInfo")
    public ApiResponse<UserBankInfoResponse> postUserPaymentInfo(@RequestBody UserBankInfoRequest userBankInfoRequest) {
        Long userId = baseController.getCurrentUserId();
        return userPaymentService.postUserPaymentInfo(userId, userBankInfoRequest);
    }

    @GetMapping("/history/charge")
    public ApiResponse<List<TopUpResponse>> getPaymentHistory(@RequestParam String period) {
        Long userId = baseController.getCurrentUserId();
        return userPaymentService.getTopUpHistory(userId, period);
    }

    @PostMapping("/trade")
    public ApiResponse<Void> tradeTickets(@RequestParam String role, @RequestParam int ticketCount) {
        Long userId = baseController.getCurrentUserId();
        return userPaymentService.tradeTickets(userId, role, ticketCount);
    }


}