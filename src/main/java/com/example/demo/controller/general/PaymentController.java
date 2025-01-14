package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.HomeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permit/pay")
@RequiredArgsConstructor
public class PaymentController {

    @Operation(summary = "결제 api")
    @PostMapping("")
    public ApiResponse<HomeResponseDTO> payment(){
        return ApiResponse.of(SuccessStatus._OK, null);
    }

    @Operation(summary = "결제 취소 api")
    @DeleteMapping("")
    public ApiResponse<HomeResponseDTO> refund(){
        return ApiResponse.of(SuccessStatus._OK, null);
    }

}