package com.example.demo.domain.dto.Inquiry;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InquiryRequestDTO {

    @NotNull(message = "User ID must not be null")
    private Long userId;
    @NotNull(message = "Raffle ID must not be null")
    private Long raffleId;
    private String title;
    private String content;

}
