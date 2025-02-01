package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Payment.ExchangeHistoryResponse;
import com.example.demo.domain.dto.Payment.ExchangeRequest;
import com.example.demo.domain.dto.Payment.ExchangeResponse;
import com.example.demo.service.general.ExchangeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment/exchange")
public class ExchangeController extends BaseController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/")
    public ApiResponse<ExchangeResponse> exchange(@RequestBody ExchangeRequest request) {
        String userId = getCurrentUserId();
        return exchangeService.exchange(userId, request);
    }

    @GetMapping("/history")
    public ApiResponse<List<ExchangeHistoryResponse>> getExchangeHistory(@RequestParam String period) {
        String userId = getCurrentUserId();
        return exchangeService.getExchangeHistory(userId, period);
    }

}