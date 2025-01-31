package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Payment.*;
import com.example.demo.service.general.UserPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController extends BaseController {

    private final UserPaymentService userPaymentService;

    @GetMapping("/tickets")
    public ApiResponse<UserTicketResponse> getUserTickets() {
        String userId = getCurrentUserId();
        return userPaymentService.getUserTickets(userId);
    }

    @PostMapping("/bankInfo")
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(@RequestBody UserBankInfoRequest userBankInfoRequest) {
        String userId = getCurrentUserId();
        return userPaymentService.getUserPaymentInfo(userId, userBankInfoRequest);
    }

    @GetMapping("/history/charge")
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(String period) { // < 7d, 1m, 3m, 6m > 4개 중 하나
        String userId = getCurrentUserId();
        return userPaymentService.getPaymentHistory(userId, period);
    }

}