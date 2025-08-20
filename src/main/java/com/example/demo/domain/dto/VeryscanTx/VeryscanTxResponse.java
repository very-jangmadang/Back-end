package com.example.demo.domain.dto.VeryscanTx;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VeryscanTxResponse {
    private String timestamp;
    private String status;
    private String value;     // "100000000000000000" (문자열로 옴)
    private Party from;
    private Party to;

    @Data
    public static class Party {
        private String hash;      // 지갑 주소
        private Boolean is_contract;
        private String name;
    }
}
