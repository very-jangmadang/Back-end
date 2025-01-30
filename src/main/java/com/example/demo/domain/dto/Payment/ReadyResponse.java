package com.example.demo.domain.dto.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReadyResponse {
    private String tid;
    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl;
    @JsonProperty("next_redirect_mobile_url")
    private String nextRedirectMobileUrl;
    @JsonProperty("created_at")
    private String createdAt;
}