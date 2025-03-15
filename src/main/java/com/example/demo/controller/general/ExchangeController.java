package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Payment.ExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.ExchangeRequest;
import com.example.demo.domain.dto.Payment.ExchangeResponse;
import com.example.demo.service.general.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/payment")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final BaseController baseController;

    @PostMapping("/exchange")
    public ApiResponse<ExchangeResponse> exchange(@RequestBody ExchangeRequest request) {
        Long userId = baseController.getCurrentUserId();
        return exchangeService.exchange(userId, request);
    }

    @GetMapping("/history/exchange")
    public ApiResponse<List<ExchangeHistoryResponse>> getExchangeHistory(@RequestParam String period) {
        Long userId = baseController.getCurrentUserId();
        return exchangeService.getExchangeHistory(userId, period);
    }
}