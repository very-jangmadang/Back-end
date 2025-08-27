package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.ErrorStatus;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.controller.BaseController;
import com.example.demo.domain.dto.Intent.OrderIntentRequestDTO;
import com.example.demo.domain.dto.Intent.OrderIntentResponseDTO;
import com.example.demo.domain.dto.Payment.ExchangeRequest;
import com.example.demo.domain.dto.VeryscanTx.VeryscanTxRequest;
import com.example.demo.service.general.impl.ExchangeServiceImpl;
import com.example.demo.service.general.impl.OrderServiceImpl;
import com.example.demo.service.general.impl.TicketTopUpServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/tickets")
public class VaryTicketOrderController {

    private final BaseController baseController;
    private final OrderServiceImpl orderServiceImpl;
    private final TicketTopUpServiceImpl ticketTopUpServiceImpl;
    private final ExchangeServiceImpl exchangeServiceImpl;

    @Operation(summary = "주문 의도 생성(사용하지 않는 api)")
    @PostMapping("/intent")
    public ApiResponse<OrderIntentResponseDTO.CreateIntentResponseDTO> createIntent(@RequestBody OrderIntentRequestDTO.CreateIntentRequestDTO request) {
        Long userId = 25L;

        int qty = request.getQuantity();

        OrderIntentResponseDTO.CreateIntentResponseDTO response = orderServiceImpl.createIntent(userId, qty);
        return ApiResponse.of(SuccessStatus._OK, response);
    }

    @Operation(summary = "txid 받아서 티켓 구매내역 저장 및 디비 반영")
    @PostMapping("/vary")
    public ApiResponse<?> createTicket(@RequestBody VeryscanTxRequest request) {
        Long userId = baseController.getCurrentUserId();
        String txid = request.getTxid();
        if (txid == null || txid.isBlank()) {
            return ApiResponse.onFailure(ErrorStatus.PAYMENT_HISTORY_ERROR, null);
        }

        var result = ticketTopUpServiceImpl.confirmTopUp(userId, txid);
        return ApiResponse.of(SuccessStatus._OK, result);

    }

//    @Operation(summary = "티켓 환전 개수 입력받아 디비에 반영")
//    @PostMapping("/exchange")
//    public ApiResponse<?> exchangeTicket(@RequestBody ExchangeRequest request) {
//        Long userId = baseController.getCurrentUserId();
//
//        return exchangeServiceImpl.exchange(userId, request);
//    }
}
