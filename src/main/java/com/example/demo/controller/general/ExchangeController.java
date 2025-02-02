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
@RequestMapping("/payment/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final BaseController baseController;

    @PostMapping("/")
    public ApiResponse<ExchangeResponse> exchange(@RequestBody ExchangeRequest request) {
        String userEmail = baseController.getCurrentUserEmail();
        return exchangeService.exchange(userEmail, request);
    }

    @GetMapping("/history")
    public ApiResponse<List<ExchangeHistoryResponse>> getExchangeHistory(@RequestParam String period) {
        String userEmail = baseController.getCurrentUserEmail();
        return exchangeService.getExchangeHistory(userEmail, period);
    }
}