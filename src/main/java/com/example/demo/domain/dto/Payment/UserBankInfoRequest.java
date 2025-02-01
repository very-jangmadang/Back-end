package com.example.demo.domain.dto.Payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBankInfoRequest {
    @NotBlank
    private String bankName;
    @NotBlank
    private String bankNumber;
}