package com.example.demo.domain.dto.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CancelResponse {

    @JsonProperty("cid")
    private String cid;
    @JsonProperty("tid")
    private String tid;
    @JsonProperty("payment_method_type")
    private String paymentMethodType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("partner_order_id")
    private String partnerOrderId;
    @JsonProperty("partner_user_id")
    private String partnerUserId;
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("approved_at")
    private String approvedAt;
    @JsonProperty("payload")
    private String payload;
    @JsonProperty("canceled_amount")
    private Amount canceledAmount;
    @JsonProperty("approved_cancel_amount")
    private Amount approvedCancelAmount;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("canceled_at")
    private String canceledAt;

    @Getter
    @Setter
    @ToString
    public static class Amount {

        @JsonProperty("total")
        private int total;
        @JsonProperty("tax_free")
        private int taxFree;
        @JsonProperty("vat")
        private int vat;
        @JsonProperty("point")
        private int point;
        @JsonProperty("discount")
        private int discount;
        @JsonProperty("green_deposit")
        private int greenDeposit;
    }
}