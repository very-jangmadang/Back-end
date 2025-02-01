package com.example.demo.domain.dto.Payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBankInfoResponse {
    @NotBlank
    private String bankName;
    @NotBlank
    private String bankNumber;
}