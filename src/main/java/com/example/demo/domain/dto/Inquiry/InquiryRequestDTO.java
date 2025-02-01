package com.example.demo.domain.dto.Inquiry;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;

@Getter
public class InquiryRequestDTO {

    @NotNull(message = "Raffle ID must not be null")
    private Long raffleId;
    private String title;
    private String content;
}
