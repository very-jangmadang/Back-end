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
public class PaymentController {

    private final UserPaymentService userPaymentService;
    private final BaseController baseController;

    @GetMapping("/tickets")
    public ApiResponse<UserTicketResponse> getUserTickets() {
        String userId = baseController.getCurrentUserEmail();
        return userPaymentService.getUserTickets(userId);
    }

    @PostMapping("/bankInfo")
    public ApiResponse<UserBankInfoResponse> getUserPaymentInfo(@RequestBody UserBankInfoRequest userBankInfoRequest) {
        String userId = baseController.getCurrentUserEmail();
        return userPaymentService.getUserPaymentInfo(userId, userBankInfoRequest);
    }

    @GetMapping("/history/charge")
    public ApiResponse<List<PaymentResponse>> getPaymentHistory(@RequestParam String period) {
        String userId = baseController.getCurrentUserEmail();
        return userPaymentService.getPaymentHistory(userId, period);
    }
}